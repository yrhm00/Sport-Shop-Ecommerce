package be.henallux.janvier.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.henallux.janvier.dataAccess.dao.PromotionDataAccess;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.Promotion;

/**
 * Service metier des promotions.
 *
 * Aucune regle n'est codee en dur : toutes les promotions proviennent de la
 * table 'promotions'. Modifier une ligne de cette table (taux, seuil, periode,
 * produit ou categorie visee) change immediatement le comportement du site.
 *
 * Deux niveaux de promotion sont geres :
 *  - promotions PRODUIT / CATEGORIE : elles font baisser le prix unitaire ;
 *  - promotions PANIER : elles reduisent le total du panier a partir d'un seuil.
 * Un produit deja remise n'est jamais remise une seconde fois au niveau produit :
 * seule la meilleure promotion applicable lui est appliquee.
 */
@Service
public class PromotionService {

    private final PromotionDataAccess promotionDAO;

    @Autowired
    public PromotionService(PromotionDataAccess promotionDAO) {
        this.promotionDAO = promotionDAO;
    }

    /** Promotions valides maintenant. */
    public List<Promotion> getPromotionsActives() {
        return promotionDAO.findPromotionsActives(LocalDateTime.now());
    }

    /**
     * Applique au produit la meilleure promotion produit/categorie disponible.
     * Le prix d'origine est conserve dans originalPrice pour l'affichage barre.
     */
    public void appliquerPromotions(Product product) {
        appliquerPromotions(product, getPromotionsActives());
    }

    /**
     * Variante utilisee lors du traitement d'une liste : les promotions sont
     * chargees une seule fois par l'appelant au lieu d'une fois par produit.
     */
    public void appliquerPromotions(Product product, List<Promotion> promotionsActives) {
        if (product == null || product.getPrix() == null || promotionsActives == null) {
            return;
        }

        BigDecimal prixInitial = product.getPrix();
        BigDecimal meilleureReduction = BigDecimal.ZERO;

        for (Promotion promotion : promotionsActives) {
            if (!promotion.concerneProduit(product.getId(), product.getCategoryId())) {
                continue;
            }
            BigDecimal reduction = promotion.reductionPour(prixInitial);
            if (reduction.compareTo(meilleureReduction) > 0) {
                meilleureReduction = reduction;
            }
        }

        if (meilleureReduction.signum() <= 0) {
            return;
        }

        product.setOriginalPrice(prixInitial);
        product.setPrix(prixInitial.subtract(meilleureReduction).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Reduction accordee sur le total du panier (promotions de portee PANIER).
     * Le total recu tient deja compte des promotions produit : il n'y a donc pas
     * de double reduction sur une meme remise.
     */
    public BigDecimal calculateDiscount(BigDecimal totalPanier) {
        if (totalPanier == null || totalPanier.signum() <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal meilleureReduction = BigDecimal.ZERO;
        for (Promotion promotion : getPromotionsActives()) {
            if (!promotion.concernePanier(totalPanier)) {
                continue;
            }
            BigDecimal reduction = promotion.reductionPour(totalPanier);
            if (reduction.compareTo(meilleureReduction) > 0) {
                meilleureReduction = reduction;
            }
        }

        return meilleureReduction.setScale(2, RoundingMode.HALF_UP);
    }
}
