package be.henallux.janvier.model;

import java.math.BigDecimal;

/**
 * Ligne d'une commande enregistree (couche metier).
 */
public class OrderLine {

    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String taille;
    private Integer quantite;
    private BigDecimal prixUnitaire;

    public OrderLine() {
    }

    public OrderLine(Integer productId, String taille, Integer quantite, BigDecimal prixUnitaire) {
        this.productId = productId;
        this.taille = taille;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getSousTotal() {
        if (prixUnitaire == null || quantite == null) {
            return BigDecimal.ZERO;
        }
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}
