package be.henallux.janvier.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import be.henallux.janvier.model.Cart;
import be.henallux.janvier.model.Order;
import be.henallux.janvier.service.OrderService;
import be.henallux.janvier.service.PaymentService;
import be.henallux.janvier.service.PromotionService;

/**
 * Parcours de commande et de paiement.
 *
 * La commande est enregistree en base AVANT le paiement, avec le statut
 * EN_ATTENTE. Si le client abandonne sur PayPal, la commande n'est pas
 * supprimee : il choisit de payer plus tard ou de l'annuler explicitement.
 */
@Controller
@RequestMapping(value = "/commandes")
public class CommandeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandeController.class);
    private static final String PENDING_ORDER_KEY = "pendingOrderId";

    private final OrderService orderService;
    private final PromotionService promotionService;
    private final PaymentService paymentService;

    @Autowired
    public CommandeController(OrderService orderService, PromotionService promotionService,
                              PaymentService paymentService) {
        this.orderService = orderService;
        this.promotionService = promotionService;
        this.paymentService = paymentService;
    }

    /** Recapitulatif avant confirmation. */
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/panier";
        }

        cart.setDiscountAmount(promotionService.calculateDiscount(cart.getTotal()));
        model.addAttribute("cart", cart);
        return "checkout";
    }

    /** Confirmation : enregistrement de la commande puis redirection vers PayPal. */
    @PostMapping("/confirmer")
    public String confirmOrder(HttpSession session, Principal principal, HttpServletRequest request,
                               Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/panier";
        }

        cart.setDiscountAmount(promotionService.calculateDiscount(cart.getTotal()));

        // La commande est enregistree AVANT toute tentative de paiement.
        Order order = orderService.createOrder(cart, principal.getName());
        if (order == null) {
            model.addAttribute("paymentError", "error.order.creation");
            model.addAttribute("cart", cart);
            return "checkout";
        }

        session.setAttribute(PENDING_ORDER_KEY, order.getId());

        String lienPaiement = demanderPaiement(order, request);
        if (lienPaiement == null) {
            // Le paiement n'a pas pu demarrer : la commande reste en attente.
            return "redirect:/commandes/" + order.getId() + "/attente?error=payment_init";
        }
        return "redirect:" + lienPaiement;
    }

    /** Relance du paiement d'une commande restee en attente. */
    @PostMapping("/{orderId}/payer")
    public String payerCommandeEnAttente(@PathVariable Integer orderId, Principal principal,
                                         HttpServletRequest request, HttpSession session) {
        Order order = orderService.getOrder(orderId);
        if (!orderService.appartientA(order, principal.getName()) || !order.isEnAttente()) {
            return "redirect:/commandes/mes-commandes";
        }

        session.setAttribute(PENDING_ORDER_KEY, order.getId());
        String lienPaiement = demanderPaiement(order, request);
        if (lienPaiement == null) {
            return "redirect:/commandes/" + orderId + "/attente?error=payment_init";
        }
        return "redirect:" + lienPaiement;
    }

    /** Annulation explicite par le client : la commande reste en base, statut ANNULEE. */
    @PostMapping("/{orderId}/annuler")
    public String annulerCommande(@PathVariable Integer orderId, Principal principal,
                                  HttpSession session) {
        Order order = orderService.getOrder(orderId);
        if (orderService.appartientA(order, principal.getName()) && order.isEnAttente()) {
            orderService.annuler(orderId);
            if (orderId.equals(session.getAttribute(PENDING_ORDER_KEY))) {
                session.removeAttribute(PENDING_ORDER_KEY);
            }
            return "redirect:/commandes/mes-commandes?annulee";
        }
        return "redirect:/commandes/mes-commandes";
    }

    /** Retour de PayPal apres validation du paiement. */
    @GetMapping("/pay/success")
    public String handlePaySuccess(@RequestParam("token") String token, HttpSession session,
                                   Principal principal) {
        Integer orderId = (Integer) session.getAttribute(PENDING_ORDER_KEY);
        Order order = orderService.getOrder(orderId);

        if (order == null || !orderService.appartientA(order, principal.getName())
                || !order.isEnAttente()
                || order.getPaypalOrderId() == null
                || !order.getPaypalOrderId().equals(token)) {
            LOGGER.warn("Retour PayPal sans correspondance pour la commande {}", orderId);
            return "redirect:/commandes/mes-commandes";
        }

        // Controle immediat avant la capture pour eviter d'encaisser une commande
        // dont le stock n'est deja plus disponible.
        if (!orderService.stockDisponible(orderId)) {
            return "redirect:/commandes/" + orderId + "/attente?error=stock_unavailable";
        }

        if (!paymentService.captureOrder(token, orderId, order.getMontantTotal())) {
            LOGGER.warn("Capture PayPal refusee pour la commande {}", orderId);
            return "redirect:/commandes/" + orderId + "/attente?error=payment_failed";
        }

        try {
            if (!orderService.marquerPayee(orderId)) {
                return "redirect:/commandes/mes-commandes";
            }
        } catch (IllegalStateException e) {
            LOGGER.error("Stock impossible a mettre a jour pour la commande deja capturee {}", orderId, e);
            return "redirect:/commandes/" + orderId + "/attente?error=stock_unavailable";
        }
        session.removeAttribute(PENDING_ORDER_KEY);

        // Le panier n'est vide qu'une fois le paiement reellement encaisse.
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.clear();
            session.setAttribute("cart", cart);
        }

        return "redirect:/commandes/succes";
    }

    /**
     * Retour de PayPal apres abandon : on informe le client et on lui laisse le
     * choix entre payer plus tard et annuler la commande.
     */
    @GetMapping("/pay/cancel")
    public String handlePayCancel(HttpSession session) {
        Integer orderId = (Integer) session.getAttribute(PENDING_ORDER_KEY);
        if (orderId == null) {
            return "redirect:/panier";
        }
        return "redirect:/commandes/" + orderId + "/attente?error=payment_cancelled";
    }

    /** Ecran d'une commande en attente de paiement. */
    @GetMapping("/{orderId}/attente")
    public String commandeEnAttente(@PathVariable Integer orderId, Principal principal, Model model,
                                    @RequestParam(required = false) String error) {
        Order order = orderService.getOrder(orderId);
        if (!orderService.appartientA(order, principal.getName()) || !order.isEnAttente()) {
            return "redirect:/commandes/mes-commandes";
        }

        model.addAttribute("order", order);
        model.addAttribute("erreurPaiement", error);
        return "commande-attente";
    }

    /** Historique des commandes du client connecte. */
    @GetMapping("/mes-commandes")
    public String mesCommandes(Principal principal, Model model) {
        model.addAttribute("orders", orderService.getOrdersOfUser(principal.getName()));
        return "mes-commandes";
    }

    @GetMapping("/succes")
    public String success() {
        return "commande-succes";
    }

    /** Cree la commande PayPal et renvoie l'URL d'approbation (null en cas d'echec). */
    private String demanderPaiement(Order order, HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort() + request.getContextPath();
        PaymentService.CreatedPayment createdPayment = paymentService.createOrder(
                order.getMontantTotal(), order.getId(),
                baseUrl + "/commandes/pay/success",
                baseUrl + "/commandes/pay/cancel");
        if (createdPayment == null
                || !orderService.associerPaiement(order.getId(), createdPayment.getPaypalOrderId())) {
            return null;
        }
        return createdPayment.getApprovalUrl();
    }
}
