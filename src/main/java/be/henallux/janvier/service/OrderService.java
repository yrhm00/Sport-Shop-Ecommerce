package be.henallux.janvier.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.entity.OrderEntity;
import be.henallux.janvier.dataAccess.entity.OrderLineEntity;
import be.henallux.janvier.dataAccess.entity.UserEntity;
import be.henallux.janvier.dataAccess.repository.OrderLineRepository;
import be.henallux.janvier.dataAccess.repository.OrderRepository;
import be.henallux.janvier.dataAccess.repository.UserRepository;
import be.henallux.janvier.model.Cart;
import be.henallux.janvier.model.CartItem;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderLineRepository orderLineRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Integer createOrder(Cart cart, String username, boolean isPaid) {
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }

        // Récupérer l'utilisateur
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        // Créer la commande
        OrderEntity order = new OrderEntity();
        order.setDateCommande(LocalDateTime.now());
        order.setUserId(user.getId());
        order.setPaye(isPaid);

        // Calculer le total (en prenant en compte la réduction du panier)
        order.setMontantTotal(cart.getTotalWithDiscount());
        order = orderRepository.save(order);

        // Sauvegarder les lignes de commande
        for (CartItem item : cart.getItems()) {
            OrderLineEntity line = new OrderLineEntity();
            line.setOrder(order);
            line.setOrderId(order.getId()); // Important : définir explicitement l'ID
            line.setProductId(item.getProduct().getId());
            line.setQuantite(item.getQuantite());

            // Prix unitaire au moment de la commande
            BigDecimal realPrice = item.getProduct().getPrix();
            if (realPrice == null) {
                realPrice = BigDecimal.ZERO;
            }
            line.setPrixUnitaire(realPrice);

            orderLineRepository.save(line);
        }

        return order.getId();
    }

    @Transactional
    public void updateOrderPaymentStatus(Integer orderId, boolean isPaid) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setPaye(isPaid);
            orderRepository.save(order);
        }
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        if (orderId != null && orderRepository.existsById(orderId)) {
            orderLineRepository.deleteByOrderId(orderId);
            orderRepository.deleteById(orderId);
        }
    }
}
