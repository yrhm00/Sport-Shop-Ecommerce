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
                            Locale locale) {

        Product product = productService.getProductById(productId, locale.getLanguage());
        if (product == null) {
            return "redirect:/produits?erreur=produit";
        }

        if (quantite == null || quantite <= 0) {
            return "redirect:/produits/" + productId + "?erreur=quantite";
        }

        if (!tailleValide(product, taille)) {
            return "redirect:/produits/" + productId + "?erreur=taille";
        }

        Cart cartExistant = (Cart) session.getAttribute(CART_SESSION_KEY);
        int quantiteExistante = cartExistant == null ? 0 : cartExistant.getQuantity(productId, taille);
        long quantiteTotale = (long) quantiteExistante + quantite;
        if (quantiteTotale > Integer.MAX_VALUE
                || !stockSuffisant(product, taille, (int) quantiteTotale)) {
            return "redirect:/produits/" + productId + "?erreur=stock";
        }

        Cart cart = cartExistant == null ? getCart(session) : cartExistant;
        cart.addItem(product, quantite, taille);
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/panier?ajoute";
    }

    /** Modifie la quantite d'un article du panier. */
    @PostMapping("/modifier")
    public String updateQuantity(@RequestParam Integer productId,
                                 @RequestParam Integer quantite,
                                 @RequestParam(required = false) String taille,
                                 HttpSession session,
                                 Locale locale) {

        if (quantite == null || quantite <= 0) {
            return "redirect:/panier?erreur=quantite";
        }

        Product product = productService.getProductById(productId, locale.getLanguage());
        if (product == null) {
            return "redirect:/panier?erreur=produit";
        }

        if (!tailleValide(product, taille)) {
            return "redirect:/panier?erreur=taille";
        }

        if (!stockSuffisant(product, taille, quantite)) {
            return "redirect:/panier?erreur=stock";
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
        if (product.getSizesStock() != null && !product.getSizesStock().isEmpty()) {
            Integer stockTaille = taille == null ? null : product.getSizesStock().get(taille);
            return stockTaille != null && stockTaille >= quantite;
        }
        return (taille == null || taille.isBlank())
                && product.getStock() != null && product.getStock() >= quantite;
    }

    /**
     * Un produit avec variantes exige une taille connue. Un produit sans
     * variantes refuse toute taille injectee manuellement dans la requete.
     */
    private boolean tailleValide(Product product, String taille) {
        boolean produitAvecTailles = product.getSizesStock() != null
                && !product.getSizesStock().isEmpty();
        if (produitAvecTailles) {
            return taille != null && !taille.isBlank()
                    && product.getSizesStock().containsKey(taille);
        }
        return taille == null || taille.isBlank();
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
