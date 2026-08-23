package be.henallux.janvier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.henallux.janvier.dataAccess.dao.OrderDataAccess;
import be.henallux.janvier.dataAccess.dao.ProductDataAccess;
import be.henallux.janvier.dataAccess.dao.UserDataAccess;
import be.henallux.janvier.model.Cart;
import be.henallux.janvier.model.Order;
import be.henallux.janvier.model.OrderLine;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.User;

class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderDataAccess orderDAO;

    @Mock
    private UserDataAccess userDAO;

    @Mock
    private ProductDataAccess productDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderDAO, userDAO, productDAO);
    }

    private User utilisateur() {
        User user = new User();
        user.setId(7);
        user.setUsername("user1");
        return user;
    }

    private Cart panierAvecUnProduit() {
        Product product = new Product();
        product.setId(1);
        product.setPrix(new BigDecimal("100.00"));

        Cart cart = new Cart();
        cart.addItem(product, 2, "42");
        cart.setDiscountAmount(new BigDecimal("20.00"));
        return cart;
    }

    @Test
    void laCommandeEstEnregistreeEnAttenteAvantLePaiement() {
        when(userDAO.findByUsername("user1")).thenReturn(utilisateur());
        when(productDAO.stockSuffisant(1, "42", 2)).thenReturn(true);
        when(orderDAO.create(any(Order.class), any())).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(42);
            return order;
        });

        Order order = orderService.createOrder(panierAvecUnProduit(), "user1");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderDAO).create(captor.capture(), any());

        assertEquals(Order.STATUT_EN_ATTENTE, captor.getValue().getStatut());
        assertFalse(captor.getValue().isPaye());
        assertEquals(0, new BigDecimal("180.00").compareTo(captor.getValue().getMontantTotal()));
        assertEquals(0, new BigDecimal("20.00").compareTo(captor.getValue().getMontantReduction()));
        assertEquals(42, order.getId());
    }

    @Test
    void laTailleCommandeeEstConserveeDansLaLigne() {
        when(userDAO.findByUsername("user1")).thenReturn(utilisateur());
        when(productDAO.stockSuffisant(1, "42", 2)).thenReturn(true);
        when(orderDAO.create(any(Order.class), any())).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(panierAvecUnProduit(), "user1");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OrderLine>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderDAO).create(any(Order.class), captor.capture());

        assertEquals(1, captor.getValue().size());
        assertEquals("42", captor.getValue().get(0).getTaille());
        assertEquals(2, captor.getValue().get(0).getQuantite());
    }

    @Test
    void panierVideOuUtilisateurInconnuNeCreeAucuneCommande() {
        assertNull(orderService.createOrder(new Cart(), "user1"));
        assertNull(orderService.createOrder(null, "user1"));

        when(userDAO.findByUsername("inconnu")).thenReturn(null);
        assertNull(orderService.createOrder(panierAvecUnProduit(), "inconnu"));

        verify(orderDAO, never()).create(any(), any());
    }

    @Test
    void lePaiementMarqueLaCommandePayeeEtDecrementeLeStock() {
        Order order = new Order();
        order.setId(42);
        order.setStatut(Order.STATUT_EN_ATTENTE);
        order.setLignes(List.of(new OrderLine(1, "42", 2, new BigDecimal("100.00"))));
        when(orderDAO.findByIdForUpdate(42)).thenReturn(order);
        when(productDAO.decrementerStock(1, "42", 2)).thenReturn(true);

        assertTrue(orderService.marquerPayee(42));

        verify(orderDAO).updateStatut(42, Order.STATUT_PAYEE, true);
        verify(productDAO).decrementerStock(1, "42", 2);
    }

    @Test
    void uneCommandeDejaPayeeNeDecrementePasLeStockUneSecondeFois() {
        Order order = new Order();
        order.setId(42);
        order.setStatut(Order.STATUT_PAYEE);
        order.setLignes(List.of(new OrderLine(1, "42", 2, new BigDecimal("100.00"))));
        when(orderDAO.findByIdForUpdate(42)).thenReturn(order);

        assertFalse(orderService.marquerPayee(42));

        verify(orderDAO, never()).updateStatut(anyInt(), anyString(), eq(true));
        verify(productDAO, never()).decrementerStock(anyInt(), anyString(), anyInt());
    }

    @Test
    void lAnnulationConserveLaCommandeEnBase() {
        Order order = new Order();
        order.setId(42);
        order.setStatut(Order.STATUT_EN_ATTENTE);
        when(orderDAO.findByIdForUpdate(42)).thenReturn(order);

        orderService.annuler(42);

        // La commande est marquee annulee : elle n'est jamais supprimee.
        verify(orderDAO, times(1)).updateStatut(42, Order.STATUT_ANNULEE, false);
    }

    @Test
    void uneCommandePayeeNePeutPlusEtreAnnulee() {
        Order order = new Order();
        order.setId(42);
        order.setStatut(Order.STATUT_PAYEE);
        when(orderDAO.findByIdForUpdate(42)).thenReturn(order);

        orderService.annuler(42);

        verify(orderDAO, never()).updateStatut(anyInt(), anyString(), any(Boolean.class));
    }

    @Test
    void uneCommandeNAppartientQuAuClientQuiLAPassee() {
        Order order = new Order();
        order.setUserId(7);
        when(userDAO.findByUsername("user1")).thenReturn(utilisateur());
        when(userDAO.findByUsername("pirate")).thenReturn(null);

        assertTrue(orderService.appartientA(order, "user1"));
        assertFalse(orderService.appartientA(order, "pirate"));
        assertFalse(orderService.appartientA(null, "user1"));
    }

    @Test
    void uneCommandeAnnuleeNePeutJamaisEtreMarqueePayee() {
        Order order = new Order();
        order.setId(42);
        order.setStatut(Order.STATUT_ANNULEE);
        when(orderDAO.findByIdForUpdate(42)).thenReturn(order);

        assertFalse(orderService.marquerPayee(42));

        verify(orderDAO, never()).updateStatut(anyInt(), anyString(), eq(true));
        verify(productDAO, never()).decrementerStock(anyInt(), any(), anyInt());
    }

    @Test
    void unEchecDeStockEmpecheDeMarquerLaCommandePayee() {
        Order order = new Order();
        order.setId(42);
        order.setStatut(Order.STATUT_EN_ATTENTE);
        order.setLignes(List.of(new OrderLine(1, "42", 2, new BigDecimal("100.00"))));
        when(orderDAO.findByIdForUpdate(42)).thenReturn(order);
        when(productDAO.decrementerStock(1, "42", 2)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> orderService.marquerPayee(42));

        verify(orderDAO, never()).updateStatut(anyInt(), anyString(), eq(true));
    }

    @Test
    void identifiantPaypalAssocieUniquementAUneCommandeEnAttente() {
        Order pending = new Order();
        pending.setId(42);
        pending.setStatut(Order.STATUT_EN_ATTENTE);
        when(orderDAO.findByIdForUpdate(42)).thenReturn(pending);

        assertTrue(orderService.associerPaiement(42, "PAYPAL-42"));
        verify(orderDAO).updatePaypalOrderId(42, "PAYPAL-42");

        pending.setStatut(Order.STATUT_ANNULEE);
        assertFalse(orderService.associerPaiement(42, "PAYPAL-NEW"));
    }
}
