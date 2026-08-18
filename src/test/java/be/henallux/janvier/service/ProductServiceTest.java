package be.henallux.janvier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.henallux.janvier.dataAccess.dao.ProductDataAccess;
import be.henallux.janvier.dataAccess.dao.PromotionDataAccess;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.Promotion;

class ProductServiceTest {

    private ProductService productService;

    @Mock
    private ProductDataAccess productDAO;

    @Mock
    private PromotionDataAccess promotionDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productDAO, new PromotionService(promotionDAO));
    }

    private Product produit(Integer id, String prix) {
        Product product = new Product();
        product.setId(id);
        product.setCategoryId(1);
        product.setPrix(new BigDecimal(prix));
        return product;
    }

    @Test
    void unProduitInexistantRenvoieNull() {
        when(productDAO.findById(999, "fr")).thenReturn(null);
        assertNull(productService.getProductById(999, "fr"));
    }

    @Test
    void sansPromotionEnBaseLePrixResteInchange() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        when(productDAO.findById(1, "fr")).thenReturn(produit(1, "129.99"));
        when(productDAO.findNouveautes(anyString(), anyInt())).thenReturn(new ArrayList<>());

        Product product = productService.getProductById(1, "fr");

        assertEquals(0, new BigDecimal("129.99").compareTo(product.getPrix()));
        assertFalse(product.isEnPromotion());
    }

    @Test
    void lesPromotionsDeLaBaseSontAppliqueesAuxListes() {
        Promotion promotion = new Promotion();
        promotion.setPortee(Promotion.PORTEE_CATEGORIE);
        promotion.setTypeReduction(Promotion.TYPE_POURCENTAGE);
        promotion.setValeur(new BigDecimal("10"));
        promotion.setCategoryId(1);
        promotion.setLibelle("-10% categorie");
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class))).thenReturn(List.of(promotion));
        when(productDAO.findByCategoryId(1, "fr")).thenReturn(new ArrayList<>(List.of(produit(1, "200.00"))));
        when(productDAO.findNouveautes(anyString(), anyInt())).thenReturn(new ArrayList<>());

        List<Product> products = productService.getProductsByCategory(1, "fr");

        assertEquals(0, new BigDecimal("180.00").compareTo(products.get(0).getPrix()));
        assertTrue(products.get(0).isEnPromotion());
        assertEquals("-10% categorie", products.get(0).getPromotionLibelle());
    }

    @Test
    void lesQuatreDerniersProduitsSontMarquesCommeNouveautes() {
        when(promotionDAO.findPromotionsActives(any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        Product nouveau = produit(5, "50.00");
        when(productDAO.findNouveautes(anyString(), anyInt())).thenReturn(new ArrayList<>(List.of(nouveau)));
        when(productDAO.findById(5, "fr")).thenReturn(nouveau);
        when(productDAO.findById(6, "fr")).thenReturn(produit(6, "50.00"));

        assertTrue(productService.getProductById(5, "fr").isNewArrival());
        assertFalse(productService.getProductById(6, "fr").isNewArrival());
    }
}
