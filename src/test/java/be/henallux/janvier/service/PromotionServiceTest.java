package be.henallux.janvier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.henallux.janvier.dataAccess.dao.PromotionDataAccess;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.Promotion;

/**
 * Tests du service de promotions.
 * Les regles proviennent de la base : le DAO est simule par Mockito.
 */
class PromotionServiceTest {

    private PromotionService promotionService;

    @Mock
    private PromotionDataAccess promotionDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        promotionService = new PromotionService(promotionDAO);
    }

    private Promotion promotion(String portee, String type, String valeur, String seuil,
                                Integer productId, Integer categoryId) {
        Promotion promotion = new Promotion();
        promotion.setCode("TEST");
        promotion.setLibelle("Promotion de test");
        promotion.setPortee(portee);
        promotion.setTypeReduction(type);
        promotion.setValeur(new BigDecimal(valeur));
        promotion.setSeuilMin(seuil == null ? null : new BigDecimal(seuil));
        promotion.setProductId(productId);
        promotion.setCategoryId(categoryId);
        return promotion;
    }

    private Product produit(Integer id, Integer categoryId, String prix) {
        Product product = new Product();
        product.setId(id);
        product.setCategoryId(categoryId);
        product.setPrix(new BigDecimal(prix));
        return product;
    }

    @Test
    void aucunePromotionEnBaseSignifieAucuneReduction() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        assertEquals(0, BigDecimal.ZERO.compareTo(promotionService.calculateDiscount(new BigDecimal("500.00"))));
    }

    @Test
    void reductionPanierAppliqueeAuDessusDuSeuil() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class)))
                .thenReturn(List.of(promotion(Promotion.PORTEE_PANIER, Promotion.TYPE_POURCENTAGE, "10", "100", null, null)));

        // 200 EUR > seuil de 100 EUR -> 10% = 20 EUR
        assertEquals(0, new BigDecimal("20.00").compareTo(promotionService.calculateDiscount(new BigDecimal("200.00"))));
    }

    @Test
    void reductionPanierIgnoreeSousLeSeuil() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class)))
                .thenReturn(List.of(promotion(Promotion.PORTEE_PANIER, Promotion.TYPE_POURCENTAGE, "10", "100", null, null)));

        assertEquals(0, BigDecimal.ZERO.compareTo(promotionService.calculateDiscount(new BigDecimal("50.00"))));
    }

    @Test
    void totalNulOuNegatifNeDonneAucuneReduction() {
        assertEquals(0, BigDecimal.ZERO.compareTo(promotionService.calculateDiscount(null)));
        assertEquals(0, BigDecimal.ZERO.compareTo(promotionService.calculateDiscount(BigDecimal.ZERO)));
    }

    @Test
    void promotionProduitBaisseLePrixEtConserveLePrixInitial() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class)))
                .thenReturn(List.of(promotion(Promotion.PORTEE_PRODUIT, Promotion.TYPE_POURCENTAGE, "10", null, 1, null)));

        Product product = produit(1, 4, "100.00");
        promotionService.appliquerPromotions(product);

        assertEquals(0, new BigDecimal("90.00").compareTo(product.getPrix()));
        assertEquals(0, new BigDecimal("100.00").compareTo(product.getOriginalPrice()));
    }

    @Test
    void promotionCategorieAppliqueeAuxProduitsDeLaCategorie() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class)))
                .thenReturn(List.of(promotion(Promotion.PORTEE_CATEGORIE, Promotion.TYPE_MONTANT, "5.00", null, null, 3)));

        Product dansLaCategorie = produit(10, 3, "25.00");
        Product horsCategorie = produit(11, 4, "25.00");

        promotionService.appliquerPromotions(dansLaCategorie);
        promotionService.appliquerPromotions(horsCategorie);

        assertEquals(0, new BigDecimal("20.00").compareTo(dansLaCategorie.getPrix()));
        assertEquals(0, new BigDecimal("25.00").compareTo(horsCategorie.getPrix()));
        assertNull(horsCategorie.getOriginalPrice());
    }

    @Test
    void seuleLaMeilleurePromotionEstAppliqueeAuProduit() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class))).thenReturn(Arrays.asList(
                promotion(Promotion.PORTEE_PRODUIT, Promotion.TYPE_POURCENTAGE, "10", null, 1, null),
                promotion(Promotion.PORTEE_CATEGORIE, Promotion.TYPE_POURCENTAGE, "25", null, null, 2)));

        Product product = produit(1, 2, "200.00");
        promotionService.appliquerPromotions(product);

        // La meilleure des deux (25%) est retenue, et une seule fois : pas de cumul.
        assertEquals(0, new BigDecimal("150.00").compareTo(product.getPrix()));
    }

    @Test
    void promotionPanierEtPromotionProduitNeSeCumulentPasSurLaMemeRemise() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class))).thenReturn(Arrays.asList(
                promotion(Promotion.PORTEE_PRODUIT, Promotion.TYPE_POURCENTAGE, "10", null, 1, null),
                promotion(Promotion.PORTEE_PANIER, Promotion.TYPE_POURCENTAGE, "10", "100", null, null)));

        Product product = produit(1, 2, "200.00");
        promotionService.appliquerPromotions(product);
        // Prix remise : 180 EUR. La remise panier porte sur ce montant deja remise.
        assertEquals(0, new BigDecimal("180.00").compareTo(product.getPrix()));

        BigDecimal remisePanier = promotionService.calculateDiscount(product.getPrix());
        assertEquals(0, new BigDecimal("18.00").compareTo(remisePanier));
    }
}
