package be.henallux.janvier.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/** Tests du calcul de reduction porte par la regle de promotion elle-meme. */
class PromotionTest {

    private Promotion promotion(String type, String valeur) {
        Promotion promotion = new Promotion();
        promotion.setTypeReduction(type);
        promotion.setValeur(new BigDecimal(valeur));
        return promotion;
    }

    @Test
    void reductionEnPourcentage() {
        assertEquals(0, new BigDecimal("15.00").compareTo(
                promotion(Promotion.TYPE_POURCENTAGE, "15").reductionPour(new BigDecimal("100.00"))));
    }

    @Test
    void reductionEnMontantFixe() {
        assertEquals(0, new BigDecimal("5.00").compareTo(
                promotion(Promotion.TYPE_MONTANT, "5.00").reductionPour(new BigDecimal("25.00"))));
    }

    @Test
    void laReductionNeDepasseJamaisLeMontant() {
        assertEquals(0, new BigDecimal("10.00").compareTo(
                promotion(Promotion.TYPE_MONTANT, "50.00").reductionPour(new BigDecimal("10.00"))));
    }

    @Test
    void montantNulOuTypeInconnuDonneZero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(promotion(Promotion.TYPE_MONTANT, "5").reductionPour(null)));
        assertEquals(0, BigDecimal.ZERO.compareTo(promotion("INCONNU", "5").reductionPour(new BigDecimal("10.00"))));
    }

    @Test
    void seuilDuPanier() {
        Promotion promotion = promotion(Promotion.TYPE_POURCENTAGE, "10");
        promotion.setPortee(Promotion.PORTEE_PANIER);
        promotion.setSeuilMin(new BigDecimal("100.00"));

        assertTrue(promotion.concernePanier(new BigDecimal("100.00")));
        assertTrue(promotion.concernePanier(new BigDecimal("150.00")));
        assertFalse(promotion.concernePanier(new BigDecimal("99.99")));
        assertFalse(promotion.concernePanier(null));
    }
}
