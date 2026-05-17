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
        System.out.println("=== OrderService.createOrder START ===");
        System.out.println("Username: " + username);
        System.out.println("isPaid: " + isPaid);
        System.out.println("Cart: " + (cart != null ? "EXISTS" : "NULL"));
        System.out.println("Cart items: " + (cart != null ? cart.getItems().size() : "N/A"));
        
        if (cart == null || cart.getItems().isEmpty()) {
            System.err.println("Cart is null or empty, returning null");
            return null;
        }

        // Récupérer l'utilisateur
        System.out.println("Fetching user from database...");
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            System.err.println("User not found: " + username);
            return null;
        }
        System.out.println("User found: ID=" + user.getId());

        // Créer la commande
        System.out.println("Creating order entity...");
        OrderEntity order = new OrderEntity();
        order.setDateCommande(LocalDateTime.now());
        order.setUserId(user.getId());
        order.setPaye(isPaid);
        
        // Calculer le total (en prenant en compte la réduction du panier)
        BigDecimal total = cart.getTotalWithDiscount();
        System.out.println("Order total: " + total);
        order.setMontantTotal(total);

        try {
            System.out.println("Saving order to database...");
            order = orderRepository.save(order);
            System.out.println("Order saved with ID: " + order.getId());
        } catch (Exception e) {
            System.err.println("ERROR saving order: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        // Sauvegarder les lignes
        System.out.println("Saving order lines...");
        for (CartItem item : cart.getItems()) {
            System.out.println("  - Product ID: " + item.getProduct().getId() + ", Qty: " + item.getQuantite());
            OrderLineEntity line = new OrderLineEntity();
            line.setOrder(order);
            line.setOrderId(order.getId()); // Important : définir explicitement l'ID
            line.setProductId(item.getProduct().getId());
            line.setQuantite(item.getQuantite());
            
            // Gestion du prix (avec promotion potentielle)
            BigDecimal realPrice = item.getProduct().getPrix(); 
            if (realPrice == null) {
                realPrice = BigDecimal.ZERO;
            }
            line.setPrixUnitaire(realPrice);
            
            try {
                orderLineRepository.save(line);
                System.out.println("  - Order line saved");
            } catch (Exception e) {
                System.err.println("  - ERROR saving order line: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("=== OrderService.createOrder END - Returning ID: " + order.getId() + " ===");
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
