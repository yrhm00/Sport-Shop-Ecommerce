package be.henallux.janvier.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Product {

    private Integer id;
    private Integer categoryId;
    private String code;
    private String nom;
    private String description;
    private BigDecimal prix;
    private BigDecimal originalPrice; // Prix avant réduction (si applicable)
    private Integer stock;
    private String imageUrl;
    private String categoryNom; // Pour l'affichage
    private Map<String, Integer> sizesStock = new HashMap<>(); // Taille -> Stock
    private boolean isNewArrival = false; // Indique si le produit est nouveau
    private String promotionLibelle; // Libelle de la promotion appliquee (issu de la table promotions)
    private java.time.LocalDateTime createdAt; // Date de création du produit

    // Constructeurs
    public Product() {
    }

    public Product(Integer categoryId, String code, String nom, String description, BigDecimal prix, Integer stock) {
        this.categoryId = categoryId;
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryNom() {
        return categoryNom;
    }

    public void setCategoryNom(String categoryNom) {
        this.categoryNom = categoryNom;
    }

    public Map<String, Integer> getSizesStock() {
        return sizesStock;
    }

    public void setSizesStock(Map<String, Integer> sizesStock) {
        this.sizesStock = sizesStock;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPromotionLibelle() {
        return promotionLibelle;
    }

    public void setPromotionLibelle(String promotionLibelle) {
        this.promotionLibelle = promotionLibelle;
    }

    /** Vrai si une promotion a fait baisser le prix de ce produit. */
    public boolean isEnPromotion() {
        return originalPrice != null && prix != null && originalPrice.compareTo(prix) > 0;
    }

    public boolean isNewArrival() {
        return isNewArrival;
    }

    public void setNewArrival(boolean isNewArrival) {
        this.isNewArrival = isNewArrival;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


