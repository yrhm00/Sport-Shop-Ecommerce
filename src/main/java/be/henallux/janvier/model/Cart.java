package be.henallux.janvier.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente le panier de l'utilisateur (stocké en session)
 */
public class Cart {

    private List<CartItem> items = new ArrayList<>();

    // Constructeurs
    public Cart() {
    }

    /**
     * Ajoute un produit au panier ou augmente sa quantité s'il existe déjà
     */
    public void addItem(Product product, Integer quantite, String taille) {
        if (product == null || quantite == null || quantite <= 0) {
            return;
        }

        // Chercher si le produit est déjà dans le panier (avec la même taille)
        CartItem existingItem = findItemByProductAndSize(product.getId(), taille);
        
        if (existingItem != null) {
            // Augmenter la quantité
            existingItem.setQuantite(existingItem.getQuantite() + quantite);
        } else {
            // Ajouter un nouvel article
            items.add(new CartItem(product, quantite, taille));
        }
    }

    public void addItem(Product product, Integer quantite) {
        addItem(product, quantite, null);
    }

    /** Quantite deja presente pour un produit et une taille donnes. */
    public int getQuantity(Integer productId, String taille) {
        CartItem item = findItemByProductAndSize(productId, taille);
        return item == null || item.getQuantite() == null ? 0 : item.getQuantite();
    }

    /**
     * Modifie la quantité d'un produit dans le panier
     */
    public void updateQuantity(Integer productId, Integer quantite, String taille) {
        if (quantite == null || quantite <= 0) {
            removeItem(productId, taille);
            return;
        }

        CartItem item = findItemByProductAndSize(productId, taille);
        if (item != null) {
            item.setQuantite(quantite);
        }
    }

    /**
     * Supprime un produit du panier 
     */
    public void removeItem(Integer productId, String taille) {
        items.removeIf(item -> item.getProduct().getId().equals(productId) &&
            ((item.getTaille() == null && taille == null) || (item.getTaille() != null && item.getTaille().equals(taille))));
    }

    /**
     * Vide complètement le panier
     */
    public void clear() {
        items.clear();
        discountAmount = BigDecimal.ZERO;
    }

    /**
     * Trouve un article par son productId
     */
    private CartItem findItemByProductAndSize(Integer productId, String taille) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId) && 
                       ((item.getTaille() == null && taille == null) || (item.getTaille() != null && item.getTaille().equals(taille))))
                .findFirst()
                .orElse(null);
    }



    /**
     * Calcule le total du panier
     */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSousTotal());
        }
        return total;
    }

    /**
     * Retourne le nombre total d'articles dans le panier
     */
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantite)
                .sum();
    }

    private BigDecimal discountAmount = BigDecimal.ZERO;

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalWithDiscount() {
        return getTotal().subtract(discountAmount);
    }

    // Getters et Setters
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}

