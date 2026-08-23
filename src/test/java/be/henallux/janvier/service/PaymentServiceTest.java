package be.henallux.janvier.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.Order;
import com.paypal.orders.PurchaseUnit;

class PaymentServiceTest {

    private PaymentService paymentService;

    @Mock
    private PayPalHttpClient payPalHttpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService(payPalHttpClient);
    }

    private Order paypalOrder(String customId, String currency, String amount) {
        PurchaseUnit purchaseUnit = new PurchaseUnit()
                .customId(customId)
                .amount(new AmountWithBreakdown().currencyCode(currency).value(amount));
        return new Order().purchaseUnits(List.of(purchaseUnit));
    }

    @Test
    void commandePaypalCorrespondQuandIdDeviseEtMontantSontCorrects() {
        assertTrue(paymentService.paiementCorrespond(
                paypalOrder("42", "EUR", "89.99"), 42, new BigDecimal("89.99")));
    }

    @Test
    void commandePaypalEstRefuseeSiLeMontantEstDifferent() {
        assertFalse(paymentService.paiementCorrespond(
                paypalOrder("42", "EUR", "1.00"), 42, new BigDecimal("89.99")));
    }

    @Test
    void commandePaypalEstRefuseeSiLaDeviseOuLaCommandeLocaleDiffere() {
        assertFalse(paymentService.paiementCorrespond(
                paypalOrder("42", "USD", "89.99"), 42, new BigDecimal("89.99")));
        assertFalse(paymentService.paiementCorrespond(
                paypalOrder("99", "EUR", "89.99"), 42, new BigDecimal("89.99")));
    }
}
