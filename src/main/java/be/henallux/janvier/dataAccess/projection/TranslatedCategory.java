package be.henallux.janvier.dataAccess.projection;

import be.henallux.janvier.dataAccess.entity.CategoryEntity;

/**
 * Resultat d'une requete categorie + traduction (voir TranslatedProduct).
 */
public class TranslatedCategory {

    private final CategoryEntity category;
    private final String nomTraduit;

    public TranslatedCategory(CategoryEntity category, String nomTraduit) {
        this.category = category;
        this.nomTraduit = nomTraduit;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public String getNomTraduit() {
        return nomTraduit;
    }
}
