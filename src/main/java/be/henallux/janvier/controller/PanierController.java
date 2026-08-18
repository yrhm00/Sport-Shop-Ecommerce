package be.henallux.janvier.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import be.henallux.janvier.model.Cart;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.service.ProductService;
import be.henallux.janvier.service.PromotionService;

/**
 * Gestion du panier (stocke en session).
 *
 * Toutes les operations qui modifient le panier sont en POST : une requete GET
 * ne doit jamais changer l'etat du serveur, et seules les requetes POST portent
 * le jeton CSRF.
 */
@Controller
@RequestMapping(value = "/panier")
public class PanierController {

    private static final String CART_SESSION_KEY = "cart";

    private final ProductService productService;
    private final PromotionService promotionService;

    @Autowired
    public PanierController(ProductService productService, PromotionService promotionService) {
        this.productService = productService;
        this.promotionService = promotionService;
    }

    /** Affiche le contenu du panier. */
    @GetMapping
    public String showPanier(HttpSession session, Model model) {
        Cart cart = getCart(session);
        cart.setDiscountAmount(promotionService.calculateDiscount(cart.getTotal()));
        model.addAttribute("cart", cart);
        return "panier";
    }

    /** Ajoute un produit au panier (accessible aussi aux visiteurs non connectes). */
    @PostMapping("/ajouter/{productId}")
    public String addToCart(@PathVariable Integer productId,
                            @RequestParam(defaultValue = "1") Integer quantite,
                            @RequestParam(required = false) String taille,
                            HttpSession session,
                            Locale locale,
                            RedirectAttributes redirectAttributes) {

        Product product = productService.getProductById(productId, locale.getLanguage());
        if (product == null) {
            redirectAttributes.addFlashAttribute("cartError", "error.product.notFound");
            return "redirect:/produits";
        }

        if (quantite == null || quantite <= 0) {
            redirectAttributes.addFlashAttribute("cartError", "error.cart.quantity");
            return "redirect:/produits/" + productId;
        }

        if (!stockSuffisant(product, taille, quantite)) {
            redirectAttributes.addFlashAttribute("cartError", "error.cart.stock");
            return "redirect:/produits/" + productId;
        }

        Cart cart = getCart(session);
        cart.addItem(product, quantite, taille);
        session.setAttribute(CART_SESSION_KEY, cart);
        redirectAttributes.addFlashAttribute("cartSuccess", "cart.added");
        return "redirect:/panier";
    }

    /** Modifie la quantite d'un article du panier. */
    @PostMapping("/modifier")
    public String updateQuantity(@RequestParam Integer productId,
                                 @RequestParam Integer quantite,
                                 @RequestParam(required = false) String taille,
                                 HttpSession session,
                                 Locale locale,
                                 RedirectAttributes redirectAttributes) {

        if (quantite == null || quantite <= 0) {
            redirectAttributes.addFlashAttribute("cartError", "error.cart.quantity");
            return "redirect:/panier";
        }

        Product product = productService.getProductById(productId, locale.getLanguage());
        if (product == null) {
            redirectAttributes.addFlashAttribute("cartError", "error.product.notFound");
            return "redirect:/panier";
        }

        if (!stockSuffisant(product, taille, quantite)) {
            redirectAttributes.addFlashAttribute("cartError", "error.cart.stock");
            return "redirect:/panier";
        }

        Cart cart = getCart(session);
        cart.updateQuantity(productId, quantite, taille);
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/panier";
    }

    /** Supprime un article du panier. */
    @PostMapping("/supprimer/{productId}")
    public String removeFromCart(@PathVariable Integer productId,
                                 @RequestParam(required = false) String taille,
                                 HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(productId, taille);
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/panier";
    }

    /** Vide completement le panier. */
    @PostMapping("/vider")
    public String clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.clear();
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/panier";
    }

    /**
     * Verifie le stock disponible : celui de la taille choisie si le produit se
     * decline en tailles, sinon le stock global.
     */
    private boolean stockSuffisant(Product product, String taille, Integer quantite) {
        if (taille != null && !taille.isBlank()
                && product.getSizesStock() != null && product.getSizesStock().containsKey(taille)) {
            Integer stockTaille = product.getSizesStock().get(taille);
            return stockTaille != null && stockTaille >= quantite;
        }
        return product.getStock() != null && product.getStock() >= quantite;
    }

    /** Recupere le panier de la session ou en cree un nouveau. */
    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }
}
