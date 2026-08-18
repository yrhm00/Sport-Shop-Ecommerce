package be.henallux.janvier.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Commande enregistree (couche metier).
 *
 * Une commande est creee AVANT le paiement avec le statut EN_ATTENTE, puis
 * passe a PAYEE ou ANNULEE. Elle n'est jamais supprimee.
 */
public class Order {

    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_PAYEE = "PAYEE";
    public static final String STATUT_ANNULEE = "ANNULEE";

    private Integer id;
    private Integer userId;
    private LocalDateTime dateCommande;
    private BigDecimal montantTotal;
    private BigDecimal montantReduction = BigDecimal.ZERO;
    private boolean paye;
    private String statut = STATUT_EN_ATTENTE;
    private List<OrderLine> lignes = new ArrayList<>();

    public Order() {
    }

    public boolean isEnAttente() {
        return STATUT_EN_ATTENTE.equals(statut);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public BigDecimal getMontantReduction() {
        return montantReduction;
    }

    public void setMontantReduction(BigDecimal montantReduction) {
        this.montantReduction = montantReduction;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<OrderLine> getLignes() {
        return lignes;
    }

    public void setLignes(List<OrderLine> lignes) {
        this.lignes = lignes;
    }
}
