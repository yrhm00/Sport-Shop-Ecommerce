package be.henallux.janvier.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private final PayPalHttpClient payPalHttpClient;

    @Autowired
    public PaymentService(PayPalHttpClient payPalHttpClient) {
        this.payPalHttpClient = payPalHttpClient;
    }

    /**
     * Cree la commande cote PayPal et renvoie l'URL d'approbation.
     * Renvoie null si PayPal ne repond pas ou refuse la demande.
     */
    public String createOrder(BigDecimal totalAmount, String returnUrl, String cancelUrl) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.intent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
                .brandName("JogginApp E-Shop")
                .landingPage("BILLING")
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl);
        orderRequest.applicationContext(applicationContext);

        List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .invoiceId("INV-" + java.util.UUID.randomUUID())
                .amount(new AmountWithBreakdown()
                        .currencyCode("EUR")
                        .value(totalAmount.setScale(2, java.math.RoundingMode.HALF_UP).toString()));
        purchaseUnitRequests.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnitRequests);

        try {
            Order order = payPalHttpClient.execute(new OrdersCreateRequest().requestBody(orderRequest)).result();
            LOGGER.info("Commande PayPal creee : {} (statut {})", order.id(), order.status());

            return order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Lien d'approbation absent de la reponse PayPal"))
                    .href();
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Creation de la commande PayPal impossible", e);
            return null;
        }
    }

    /** Encaisse le paiement. Renvoie vrai uniquement si PayPal confirme la capture. */
    public boolean captureOrder(String orderId) {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderRequest());

        try {
            Order order = payPalHttpClient.execute(request).result();
            LOGGER.info("Capture PayPal pour {} : statut {}", orderId, order.status());
            return "COMPLETED".equals(order.status());
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Capture du paiement PayPal impossible", e);
            return false;
        }
    }
}
