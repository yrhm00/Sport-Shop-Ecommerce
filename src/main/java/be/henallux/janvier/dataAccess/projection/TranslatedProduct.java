package be.henallux.janvier.dataAccess.projection;

import be.henallux.janvier.dataAccess.entity.ProductEntity;

/**
 * Resultat d'une requete produit + traduction.
 *
 * Le nom et la description traduits proviennent de la JOINTURE avec la table
 * unique 'translations' : ils valent null si aucune traduction n'existe pour la
 * langue demandee (on retombe alors sur les libelles de la table 'products').
 */
public class TranslatedProduct {

    private final ProductEntity product;
    private final String nomTraduit;
    private final String descriptionTraduite;

    public TranslatedProduct(ProductEntity product, String nomTraduit, String descriptionTraduite) {
        this.product = product;
        this.nomTraduit = nomTraduit;
        this.descriptionTraduite = descriptionTraduite;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public String getNomTraduit() {
        return nomTraduit;
    }

    public String getDescriptionTraduite() {
        return descriptionTraduite;
    }
}
