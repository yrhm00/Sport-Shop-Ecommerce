package be.henallux.janvier.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.dao.OrderDataAccess;
import be.henallux.janvier.dataAccess.dao.ProductDataAccess;
import be.henallux.janvier.dataAccess.dao.UserDataAccess;
import be.henallux.janvier.model.Cart;
import be.henallux.janvier.model.CartItem;
import be.henallux.janvier.model.Order;
import be.henallux.janvier.model.OrderLine;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.User;

@Service
public class OrderService {

    private final OrderDataAccess orderDAO;
    private final UserDataAccess userDAO;
    private final ProductDataAccess productDAO;

    @Autowired
    public OrderService(OrderDataAccess orderDAO, UserDataAccess userDAO, ProductDataAccess productDAO) {
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
        this.productDAO = productDAO;
    }

    /**
     * Enregistre la commande AVANT le paiement, avec le statut EN_ATTENTE.
     * Renvoie null si le panier est vide ou si l'utilisateur est introuvable.
     */
    @Transactional
    public Order createOrder(Cart cart, String username) {
        if (cart == null || cart.getItems().isEmpty() || username == null) {
            return null;
        }

        User user = userDAO.findByUsername(username);
        if (user == null || user.getId() == null) {
            return null;
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setDateCommande(LocalDateTime.now());
        order.setMontantTotal(cart.getTotalWithDiscount());
        order.setMontantReduction(cart.getDiscountAmount());
        order.setPaye(false);
        order.setStatut(Order.STATUT_EN_ATTENTE);

        List<OrderLine> lignes = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product == null || product.getId() == null) {
                continue;
            }
            lignes.add(new OrderLine(product.getId(), item.getTaille(), item.getQuantite(), product.getPrix()));
        }

        if (lignes.isEmpty()) {
            return null;
        }

        return orderDAO.create(order, lignes);
    }

    public Order getOrder(Integer orderId) {
        return orderDAO.findById(orderId);
    }

    public List<Order> getOrdersOfUser(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return new ArrayList<>();
        }
        return orderDAO.findByUserId(user.getId());
    }

    /** Verifie que la commande appartient bien a l'utilisateur passe en parametre. */
    public boolean appartientA(Order order, String username) {
        if (order == null || username == null) {
            return false;
        }
        User user = userDAO.findByUsername(username);
        return user != null && user.getId() != null && user.getId().equals(order.getUserId());
    }

    /**
     * Marque la commande comme payee et retire les quantites commandees du stock.
     */
    @Transactional
    public void marquerPayee(Integer orderId) {
        Order order = orderDAO.findById(orderId);
        if (order == null || Order.STATUT_PAYEE.equals(order.getStatut())) {
            return;
        }

        orderDAO.updateStatut(orderId, Order.STATUT_PAYEE, true);

        for (OrderLine ligne : order.getLignes()) {
            productDAO.decrementerStock(ligne.getProductId(), ligne.getTaille(), ligne.getQuantite());
        }
    }

    /**
     * Marque la commande comme annulee. La commande reste en base : la trace de
     * l'enregistrement avant paiement est conservee.
     */
    @Transactional
    public void annuler(Integer orderId) {
        Order order = orderDAO.findById(orderId);
        if (order == null || !order.isEnAttente()) {
            return;
        }
        orderDAO.updateStatut(orderId, Order.STATUT_ANNULEE, false);
    }
}
