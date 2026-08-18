package be.henallux.janvier.dataAccess.dao;

import java.util.List;

import be.henallux.janvier.model.Order;
import be.henallux.janvier.model.OrderLine;

public interface OrderDataAccess {

    /** Enregistre la commande et ses lignes, et renvoie la commande avec son identifiant. */
    Order create(Order order, List<OrderLine> lignes);

    Order findById(Integer orderId);

    List<OrderLine> findLignes(Integer orderId);

    List<Order> findByUserId(Integer userId);

    /** Met a jour le statut (et le drapeau paye) d'une commande existante. */
    void updateStatut(Integer orderId, String statut, boolean paye);
}
