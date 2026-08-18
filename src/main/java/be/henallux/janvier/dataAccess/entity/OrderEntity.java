package be.henallux.janvier.dataAccess.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_PAYEE = "PAYEE";
    public static final String STATUT_ANNULEE = "ANNULEE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "date_commande")
    private LocalDateTime dateCommande;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "montant_reduction", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantReduction = BigDecimal.ZERO;

    @Column(name = "paye")
    private Boolean paye = false;

    /** EN_ATTENTE : enregistree avant paiement / PAYEE / ANNULEE. */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = STATUT_EN_ATTENTE;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderLineEntity> orderLines = new HashSet<>();

    // Constructeurs
    public OrderEntity() {
    }

    public OrderEntity(Integer userId, BigDecimal montantTotal) {
        this.userId = userId;
        this.montantTotal = montantTotal;
        this.dateCommande = LocalDateTime.now();
        this.paye = false;
    }

    // Getters et Setters
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Boolean getPaye() {
        return paye;
    }

    public void setPaye(Boolean paye) {
        this.paye = paye;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<OrderLineEntity> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Set<OrderLineEntity> orderLines) {
        this.orderLines = orderLines;
    }
}


