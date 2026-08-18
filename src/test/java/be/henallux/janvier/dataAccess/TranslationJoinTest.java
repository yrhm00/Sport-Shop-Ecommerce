package be.henallux.janvier.dataAccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import be.henallux.janvier.dataAccess.entity.CategoryEntity;
import be.henallux.janvier.dataAccess.entity.ProductEntity;
import be.henallux.janvier.dataAccess.entity.PromotionEntity;
import be.henallux.janvier.dataAccess.entity.TranslationEntity;
import be.henallux.janvier.dataAccess.projection.TranslatedCategory;
import be.henallux.janvier.dataAccess.projection.TranslatedProduct;
import be.henallux.janvier.dataAccess.repository.CategoryRepository;
import be.henallux.janvier.dataAccess.repository.ProductRepository;
import be.henallux.janvier.dataAccess.repository.PromotionRepository;

/**
 * Verifie la table UNIQUE de traduction et les jointures qui la lisent,
 * sur une base en memoire.
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never"
})
class TranslationJoinTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    private Integer categoryId;
    private Integer productId;

    @BeforeEach
    void chargerLesDonnees() {
        CategoryEntity category = new CategoryEntity("MONTRES", "Montres et Trackers", "/images/watches.png");
        entityManager.persist(category);
        categoryId = category.getId();

        ProductEntity product = new ProductEntity();
        product.setCategoryId(categoryId);
        product.setCode("WATCH-001");
        product.setNom("Garmin Forerunner 255");
        product.setDescription("GPS integre");
        product.setPrix(new BigDecimal("349.99"));
        product.setStock(25);
        product.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product);
        productId = product.getId();

        entityManager.persist(new TranslationEntity(TranslationEntity.TYPE_CATEGORY, categoryId, "en",
                TranslationEntity.CHAMP_NOM, "Watches and Trackers"));
        entityManager.persist(new TranslationEntity(TranslationEntity.TYPE_PRODUCT, productId, "en",
                TranslationEntity.CHAMP_NOM, "Garmin Forerunner 255"));
        entityManager.persist(new TranslationEntity(TranslationEntity.TYPE_PRODUCT, productId, "en",
                TranslationEntity.CHAMP_DESCRIPTION, "Built-in GPS"));

        entityManager.flush();
    }

    @Test
    void laJointureRamenLaTraductionAnglaiseDeLaCategorie() {
        TranslatedCategory traduite = categoryRepository.findByIdTraduit(categoryId, "en");

        assertNotNull(traduite);
        assertEquals("Watches and Trackers", traduite.getNomTraduit());
        assertEquals("Montres et Trackers", traduite.getCategory().getNom());
    }

    @Test
    void sansTraductionLaJointureRenvoieNullEtOnGardeLeLibelleDeBase() {
        // Aucune ligne 'fr' n'a ete inseree : la jointure externe renvoie null.
        TranslatedCategory traduite = categoryRepository.findByCodeTraduit("MONTRES", "fr");

        assertNotNull(traduite);
        assertNull(traduite.getNomTraduit());
        assertEquals("Montres et Trackers", traduite.getCategory().getNom());
    }

    @Test
    void laJointureRamenLeNomEtLaDescriptionTraduitsDuProduit() {
        TranslatedProduct traduit = productRepository.findByIdTraduit(productId, "en");

        assertNotNull(traduit);
        assertEquals("Garmin Forerunner 255", traduit.getNomTraduit());
        assertEquals("Built-in GPS", traduit.getDescriptionTraduite());
    }

    @Test
    void lesProduitsDUneCategorieSontRamenesAvecLeursTraductions() {
        List<TranslatedProduct> produits = productRepository.findByCategoryIdTraduit(categoryId, "en");

        assertEquals(1, produits.size());
        assertEquals("Built-in GPS", produits.get(0).getDescriptionTraduite());
    }

    @Test
    void lesProduitsEnPromotionViennentDeLaTablePromotions() {
        // Aucune promotion active : la liste est vide.
        assertTrue(productRepository.findEnPromotionTraduit("fr", LocalDateTime.now()).isEmpty());

        PromotionEntity promotion = new PromotionEntity();
        promotion.setCode("PROMO-MONTRES");
        promotion.setLibelle("Promotion montres");
        promotion.setPortee(PromotionEntity.PORTEE_CATEGORIE);
        promotion.setTypeReduction(PromotionEntity.TYPE_POURCENTAGE);
        promotion.setValeur(new BigDecimal("15.00"));
        promotion.setCategoryId(categoryId);
        promotion.setActive(true);
        entityManager.persist(promotion);
        entityManager.flush();

        // Une fois la ligne inseree en base, le produit apparait en promotion.
        assertEquals(1, productRepository.findEnPromotionTraduit("fr", LocalDateTime.now()).size());
    }

    @Test
    void unePromotionDesactiveeOuExpireeNEstPasRetenue() {
        PromotionEntity expiree = new PromotionEntity();
        expiree.setCode("PROMO-NOEL");
        expiree.setLibelle("Promotion de Noel");
        expiree.setPortee(PromotionEntity.PORTEE_PANIER);
        expiree.setTypeReduction(PromotionEntity.TYPE_POURCENTAGE);
        expiree.setValeur(new BigDecimal("20.00"));
        expiree.setActive(true);
        expiree.setDateDebut(LocalDateTime.now().minusDays(30));
        expiree.setDateFin(LocalDateTime.now().minusDays(10));
        entityManager.persist(expiree);

        PromotionEntity desactivee = new PromotionEntity();
        desactivee.setCode("PROMO-OFF");
        desactivee.setLibelle("Promotion desactivee");
        desactivee.setPortee(PromotionEntity.PORTEE_PANIER);
        desactivee.setTypeReduction(PromotionEntity.TYPE_POURCENTAGE);
        desactivee.setValeur(new BigDecimal("30.00"));
        desactivee.setActive(false);
        entityManager.persist(desactivee);
        entityManager.flush();

        assertTrue(promotionRepository.findPromotionsActives(LocalDateTime.now()).isEmpty());
    }

    @Test
    void leStockNePeutPasDevenirNegatif() {
        assertEquals(1, productRepository.decrementerStock(productId, 5));
        entityManager.clear();
        assertEquals(20, entityManager.find(ProductEntity.class, productId).getStock());

        // Quantite superieure au stock : aucune ligne modifiee.
        assertEquals(0, productRepository.decrementerStock(productId, 1000));
        entityManager.clear();
        assertEquals(20, entityManager.find(ProductEntity.class, productId).getStock());
    }
}
