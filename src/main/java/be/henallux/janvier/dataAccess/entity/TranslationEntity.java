package be.henallux.janvier.dataAccess.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Table UNIQUE de traduction pour toutes les entites de la base.
 *
 * Une ligne = la traduction d'un champ d'une entite dans une langue.
 * Le rattachement a l'entite traduite se fait par (entityType, entityId) et la
 * selection de la bonne ligne par JOINTURE dans les requetes des repositories.
 */
@Entity
@Table(name = "translations")
public class TranslationEntity implements Serializable {

    /** Valeurs possibles pour la colonne entity_type. */
    public static final String TYPE_CATEGORY = "CATEGORY";
    public static final String TYPE_PRODUCT = "PRODUCT";

    /** Valeurs possibles pour la colonne champ. */
    public static final String CHAMP_NOM = "nom";
    public static final String CHAMP_DESCRIPTION = "description";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    @Column(name = "locale", nullable = false, length = 5)
    private String locale;

    @Column(name = "champ", nullable = false, length = 30)
    private String champ;

    @Column(name = "valeur", nullable = false, columnDefinition = "TEXT")
    private String valeur;

    public TranslationEntity() {
    }

    public TranslationEntity(String entityType, Integer entityId, String locale, String champ, String valeur) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.locale = locale;
        this.champ = champ;
        this.valeur = valeur;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getChamp() {
        return champ;
    }

    public void setChamp(String champ) {
        this.champ = champ;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}
