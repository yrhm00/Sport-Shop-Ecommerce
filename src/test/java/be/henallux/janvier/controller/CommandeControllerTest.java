package be.henallux.janvier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;

import be.henallux.janvier.model.Cart;
import be.henallux.janvier.model.Order;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.service.OrderService;
import be.henallux.janvier.service.PaymentService;
import be.henallux.janvier.service.PromotionService;

class CommandeControllerTest {

    private CommandeController controller;
    private final Principal principal = () -> "user1";

    @Mock
    private OrderService orderService;

    @Mock
    private PromotionService promotionService;

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new CommandeController(orderService, promotionService, paymentService);
    }

    private Order commande(String statut) {
        Order order = new Order();
        order.setId(42);
        order.setUserId(7);
        order.setStatut(statut);
        order.setMontantTotal(new BigDecimal("89.99"));
        order.setPaypalOrderId("PAYPAL-42");
        return order;
    }

    @Test
    void leTokenPaypalDoitCorrespondreALaCommandeLocale() {
        Order order = commande(Order.STATUT_EN_ATTENTE);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("pendingOrderId", 42);
        when(orderService.getOrder(42)).thenReturn(order);
        when(orderService.appartientA(order, "user1")).thenReturn(true);

        String result = controller.handlePaySuccess("AUTRE-TOKEN", session, principal);

        assertEquals("redirect:/commandes/mes-commandes", result);
        verify(paymentService, never()).captureOrder(anyString(), eq(42), eq(order.getMontantTotal()));
        verify(orderService, never()).marquerPayee(42);
    }

    @Test
    void uneCommandeAnnuleeNePeutPasEtreCapturee() {
        Order order = commande(Order.STATUT_ANNULEE);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("pendingOrderId", 42);
        when(orderService.getOrder(42)).thenReturn(order);
        when(orderService.appartientA(order, "user1")).thenReturn(true);

        String result = controller.handlePaySuccess("PAYPAL-42", session, principal);

        assertEquals("redirect:/commandes/mes-commandes", result);
        verify(paymentService, never()).captureOrder(anyString(), eq(42), eq(order.getMontantTotal()));
    }

    @Test
    void leStockEstControleAvantLaCapture() {
        Order order = commande(Order.STATUT_EN_ATTENTE);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("pendingOrderId", 42);
        when(orderService.getOrder(42)).thenReturn(order);
        when(orderService.appartientA(order, "user1")).thenReturn(true);
        when(orderService.stockDisponible(42)).thenReturn(false);

        String result = controller.handlePaySuccess("PAYPAL-42", session, principal);

        assertEquals("redirect:/commandes/42/attente?error=stock_unavailable", result);
        verify(paymentService, never()).captureOrder(anyString(), eq(42), eq(order.getMontantTotal()));
    }

    @Test
    void unPaiementValideMarqueLaCommandeEtVideLePanier() {
        Order order = commande(Order.STATUT_EN_ATTENTE);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("pendingOrderId", 42);
        Product product = new Product();
        product.setId(1);
        product.setPrix(new BigDecimal("89.99"));
        Cart cart = new Cart();
        cart.addItem(product, 1);
        session.setAttribute("cart", cart);

        when(orderService.getOrder(42)).thenReturn(order);
        when(orderService.appartientA(order, "user1")).thenReturn(true);
        when(orderService.stockDisponible(42)).thenReturn(true);
        when(paymentService.captureOrder("PAYPAL-42", 42, order.getMontantTotal())).thenReturn(true);
        when(orderService.marquerPayee(42)).thenReturn(true);

        String result = controller.handlePaySuccess("PAYPAL-42", session, principal);

        assertEquals("redirect:/commandes/succes", result);
        assertNull(session.getAttribute("pendingOrderId"));
        assertTrue(((Cart) session.getAttribute("cart")).getItems().isEmpty());
    }

    @Test
    void laCreationPaypalEstAssocieeAvantLaRedirection() {
        MockHttpSession session = new MockHttpSession();
        Product product = new Product();
        product.setId(1);
        product.setPrix(new BigDecimal("89.99"));
        Cart cart = new Cart();
        cart.addItem(product, 1);
        session.setAttribute("cart", cart);

        Order order = commande(Order.STATUT_EN_ATTENTE);
        when(promotionService.calculateDiscount(cart.getTotal())).thenReturn(BigDecimal.ZERO);
        when(orderService.createOrder(cart, "user1")).thenReturn(order);
        when(paymentService.createOrder(eq(order.getMontantTotal()), eq(42), anyString(), anyString()))
                .thenReturn(new PaymentService.CreatedPayment("PAYPAL-42", "https://paypal.test/approve"));
        when(orderService.associerPaiement(42, "PAYPAL-42")).thenReturn(true);

        String result = controller.confirmOrder(session, principal,
                new MockHttpServletRequest(), new ExtendedModelMap());

        assertEquals("redirect:https://paypal.test/approve", result);
        assertEquals(42, session.getAttribute("pendingOrderId"));
        verify(orderService).associerPaiement(42, "PAYPAL-42");
    }

    @Test
    void annulerLaCommandeSupprimeLaTentativePaypalDeLaSession() {
        Order order = commande(Order.STATUT_EN_ATTENTE);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("pendingOrderId", 42);
        when(orderService.getOrder(42)).thenReturn(order);
        when(orderService.appartientA(order, "user1")).thenReturn(true);

        String result = controller.annulerCommande(42, principal, session);

        assertEquals("redirect:/commandes/mes-commandes?annulee", result);
        assertNull(session.getAttribute("pendingOrderId"));
        verify(orderService).annuler(42);
    }
}
