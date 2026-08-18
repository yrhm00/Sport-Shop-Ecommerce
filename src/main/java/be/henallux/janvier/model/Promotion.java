package be.henallux.janvier.model;

import java.math.BigDecimal;

/**
 * Regle de promotion, telle qu'elle est lue depuis la table 'promotions'.
 * Objet de la couche metier : aucune dependance vers JPA.
 */
public class Promotion {

    public static final String PORTEE_PRODUIT = "PRODUIT";
    public static final String PORTEE_CATEGORIE = "CATEGORIE";
    public static final String PORTEE_PANIER = "PANIER";

    public static final String TYPE_POURCENTAGE = "POURCENTAGE";
    public static final String TYPE_MONTANT = "MONTANT";

    private Integer id;
    private String code;
    private String libelle;
    private String portee;
    private String typeReduction;
    private BigDecimal valeur;
    private BigDecimal seuilMin;
    private Integer productId;
    private Integer categoryId;

    public Promotion() {
    }

    /**
     * Calcule la reduction accordee sur un montant donne.
     * Renvoie zero si le montant est absent ou si la reduction n'a pas de sens.
     */
    public BigDecimal reductionPour(BigDecimal montant) {
        if (montant == null || valeur == null || montant.signum() <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal reduction;
        if (TYPE_POURCENTAGE.equals(typeReduction)) {
            reduction = montant.multiply(valeur).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        } else if (TYPE_MONTANT.equals(typeReduction)) {
            reduction = valeur;
        } else {
            return BigDecimal.ZERO;
        }

        // Une reduction ne peut jamais depasser le montant sur lequel elle porte.
        return reduction.min(montant).max(BigDecimal.ZERO);
    }

    /** Vrai si la promotion s'applique au produit donne (portee produit ou categorie). */
    public boolean concerneProduit(Integer produitId, Integer categorieId) {
        if (PORTEE_PRODUIT.equals(portee)) {
            return productId != null && productId.equals(produitId);
        }
        if (PORTEE_CATEGORIE.equals(portee)) {
            return categoryId != null && categoryId.equals(categorieId);
        }
        return false;
    }

    /** Vrai si la promotion porte sur le total du panier et que le seuil est atteint. */
    public boolean concernePanier(BigDecimal totalPanier) {
        if (!PORTEE_PANIER.equals(portee) || totalPanier == null) {
            return false;
        }
        return seuilMin == null || totalPanier.compareTo(seuilMin) >= 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getPortee() {
        return portee;
    }

    public void setPortee(String portee) {
        this.portee = portee;
    }

    public String getTypeReduction() {
        return typeReduction;
    }

    public void setTypeReduction(String typeReduction) {
        this.typeReduction = typeReduction;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public BigDecimal getSeuilMin() {
        return seuilMin;
    }

    public void setSeuilMin(BigDecimal seuilMin) {
        this.seuilMin = seuilMin;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
