package be.henallux.janvier.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

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
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.PurchaseUnit;
import com.paypal.orders.PurchaseUnitRequest;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private final PayPalHttpClient payPalHttpClient;

    @Autowired
    public PaymentService(PayPalHttpClient payPalHttpClient) {
        this.payPalHttpClient = payPalHttpClient;
    }

    /** Resultat minimal necessaire pour relier la commande PayPal a la commande locale. */
    public static final class CreatedPayment {
        private final String paypalOrderId;
        private final String approvalUrl;

        public CreatedPayment(String paypalOrderId, String approvalUrl) {
            this.paypalOrderId = paypalOrderId;
            this.approvalUrl = approvalUrl;
        }

        public String getPaypalOrderId() {
            return paypalOrderId;
        }

        public String getApprovalUrl() {
            return approvalUrl;
        }
    }

    /**
     * Cree la commande cote PayPal et renvoie son identifiant avec l'URL
     * d'approbation. Le customId rattache sans ambiguite le paiement a la
     * commande deja enregistree dans notre base.
     */
    public CreatedPayment createOrder(BigDecimal totalAmount, Integer localOrderId,
                                      String returnUrl, String cancelUrl) {
        if (totalAmount == null || totalAmount.signum() <= 0 || localOrderId == null) {
            return null;
        }

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
                .customId(localOrderId.toString())
                .invoiceId("ORDER-" + localOrderId + "-" + UUID.randomUUID())
                .amount(new AmountWithBreakdown()
                        .currencyCode("EUR")
                        .value(totalAmount.setScale(2, RoundingMode.HALF_UP).toString()));
        purchaseUnitRequests.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnitRequests);

        try {
            OrdersCreateRequest request = new OrdersCreateRequest();
            request.requestBody(orderRequest);
            request.prefer("return=representation");
            Order order = payPalHttpClient.execute(request).result();
            LOGGER.info("Commande PayPal creee : {} (statut {})", order.id(), order.status());

            String approvalUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Lien d'approbation absent de la reponse PayPal"))
                    .href();
            return new CreatedPayment(order.id(), approvalUrl);
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Creation de la commande PayPal impossible", e);
            return null;
        }
    }

    /**
     * Verifie la commande PayPal avant de l'encaisser, puis controle une seconde
     * fois les donnees renvoyees apres la capture.
     */
    public boolean captureOrder(String paypalOrderId, Integer localOrderId, BigDecimal expectedAmount) {
        if (paypalOrderId == null || paypalOrderId.isBlank()
                || localOrderId == null || expectedAmount == null || expectedAmount.signum() <= 0) {
            return false;
        }

        try {
            Order approvedOrder = payPalHttpClient.execute(new OrdersGetRequest(paypalOrderId)).result();
            if (approvedOrder == null || !paypalOrderId.equals(approvedOrder.id())
                    || !"APPROVED".equals(approvedOrder.status())
                    || !paiementCorrespond(approvedOrder, localOrderId, expectedAmount)) {
                LOGGER.warn("Commande PayPal {} incoherente avec la commande locale {}",
                        paypalOrderId, localOrderId);
                return false;
            }

            OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
            request.requestBody(new OrderRequest());
            request.prefer("return=representation");
            Order capturedOrder = payPalHttpClient.execute(request).result();
            LOGGER.info("Capture PayPal pour {} : statut {}", paypalOrderId,
                    capturedOrder == null ? null : capturedOrder.status());
            return capturedOrder != null
                    && paypalOrderId.equals(capturedOrder.id())
                    && "COMPLETED".equals(capturedOrder.status())
                    && paiementCorrespond(capturedOrder, localOrderId, expectedAmount);
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Capture du paiement PayPal impossible", e);
            return false;
        }
    }

    /** Controle du rattachement, de la devise et du montant d'une reponse PayPal. */
    boolean paiementCorrespond(Order paypalOrder, Integer localOrderId, BigDecimal expectedAmount) {
        if (paypalOrder == null || localOrderId == null || expectedAmount == null
                || paypalOrder.purchaseUnits() == null || paypalOrder.purchaseUnits().size() != 1) {
            return false;
        }

        PurchaseUnit purchaseUnit = paypalOrder.purchaseUnits().get(0);
        if (purchaseUnit == null || !localOrderId.toString().equals(purchaseUnit.customId())
                || purchaseUnit.amount() == null
                || !"EUR".equals(purchaseUnit.amount().currencyCode())
                || purchaseUnit.amount().value() == null) {
            return false;
        }

        try {
            BigDecimal paypalAmount = new BigDecimal(purchaseUnit.amount().value());
            return paypalAmount.compareTo(expectedAmount.setScale(2, RoundingMode.HALF_UP)) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
