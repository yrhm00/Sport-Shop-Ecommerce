package be.henallux.janvier.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests du panier (objet de session). */
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    private Product produit(Integer id, String prix) {
        Product product = new Product();
        product.setId(id);
        product.setPrix(new BigDecimal(prix));
        return product;
    }

    @Test
    void ajoutDeuxFoisLeMemeProduitCumuleLesQuantites() {
        cart.addItem(produit(1, "10.00"), 2, null);
        cart.addItem(produit(1, "10.00"), 3, null);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getTotalItems());
        assertEquals(0, new BigDecimal("50.00").compareTo(cart.getTotal()));
    }

    @Test
    void deuxTaillesDuMemeProduitSontDeuxLignesDistinctes() {
        cart.addItem(produit(1, "10.00"), 1, "M");
        cart.addItem(produit(1, "10.00"), 1, "L");

        assertEquals(2, cart.getItems().size());
    }

    @Test
    void quantiteNulleOuNegativeEstRefusee() {
        cart.addItem(produit(1, "10.00"), 0, null);
        cart.addItem(produit(1, "10.00"), -5, null);

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void modifierUneQuantiteAZeroSupprimeLaLigne() {
        cart.addItem(produit(1, "10.00"), 2, null);
        cart.updateQuantity(1, 0, null);

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void suppressionCibleLaBonneTaille() {
        cart.addItem(produit(1, "10.00"), 1, "M");
        cart.addItem(produit(1, "10.00"), 1, "L");
        cart.removeItem(1, "M");

        assertEquals(1, cart.getItems().size());
        assertEquals("L", cart.getItems().get(0).getTaille());
    }

    @Test
    void leTotalTientCompteDeLaReduction() {
        cart.addItem(produit(1, "100.00"), 2, null);
        cart.setDiscountAmount(new BigDecimal("20.00"));

        assertEquals(0, new BigDecimal("200.00").compareTo(cart.getTotal()));
        assertEquals(0, new BigDecimal("180.00").compareTo(cart.getTotalWithDiscount()));
    }

    @Test
    void viderLePanierRemetLaReductionAZero() {
        cart.addItem(produit(1, "100.00"), 2, null);
        cart.setDiscountAmount(new BigDecimal("20.00"));
        cart.clear();

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getDiscountAmount()));
    }
}
