package be.henallux.janvier.dataAccess.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Regle de promotion stockee en base.
 *
 * Aucune promotion n'est codee en dur dans l'application : le service metier
 * (PromotionService) lit ces lignes et applique les reductions correspondantes.
 */
@Entity
@Table(name = "promotions")
public class PromotionEntity implements Serializable {

    /** Portees possibles. */
    public static final String PORTEE_PRODUIT = "PRODUIT";
    public static final String PORTEE_CATEGORIE = "CATEGORIE";
    public static final String PORTEE_PANIER = "PANIER";

    /** Types de reduction possibles. */
    public static final String TYPE_POURCENTAGE = "POURCENTAGE";
    public static final String TYPE_MONTANT = "MONTANT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "libelle", nullable = false, length = 150)
    private String libelle;

    @Column(name = "portee", nullable = false, length = 20)
    private String portee;

    @Column(name = "type_reduction", nullable = false, length = 20)
    private String typeReduction;

    @Column(name = "valeur", nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;

    @Column(name = "seuil_min", precision = 10, scale = 2)
    private BigDecimal seuilMin;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    public PromotionEntity() {
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

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
