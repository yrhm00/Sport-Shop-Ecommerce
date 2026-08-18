package be.henallux.janvier.dataAccess.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.entity.OrderEntity;
import be.henallux.janvier.dataAccess.entity.OrderLineEntity;
import be.henallux.janvier.dataAccess.repository.OrderLineRepository;
import be.henallux.janvier.dataAccess.repository.OrderRepository;
import be.henallux.janvier.dataAccess.util.ProviderConverter;
import be.henallux.janvier.model.Order;
import be.henallux.janvier.model.OrderLine;

@Service
@Transactional
public class OrderDAO implements OrderDataAccess {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProviderConverter converter;

    @Autowired
    public OrderDAO(OrderRepository orderRepository, OrderLineRepository orderLineRepository,
                    ProviderConverter converter) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.converter = converter;
    }

    @Override
    public Order create(Order order, List<OrderLine> lignes) {
        if (order == null || lignes == null || lignes.isEmpty()) {
            return null;
        }

        OrderEntity entity = new OrderEntity();
        entity.setUserId(order.getUserId());
        entity.setDateCommande(order.getDateCommande());
        entity.setMontantTotal(order.getMontantTotal());
        entity.setMontantReduction(order.getMontantReduction());
        entity.setPaye(order.isPaye());
        entity.setStatut(order.getStatut());
        entity = orderRepository.save(entity);

        for (OrderLine ligne : lignes) {
            OrderLineEntity ligneEntity = new OrderLineEntity();
            ligneEntity.setOrderId(entity.getId());
            ligneEntity.setProductId(ligne.getProductId());
            ligneEntity.setTaille(ligne.getTaille());
            ligneEntity.setQuantite(ligne.getQuantite());
            ligneEntity.setPrixUnitaire(ligne.getPrixUnitaire());
            orderLineRepository.save(ligneEntity);
        }

        Order enregistree = converter.orderEntityToModel(entity);
        enregistree.setLignes(new ArrayList<>(lignes));
        return enregistree;
    }

    @Override
    public Order findById(Integer orderId) {
        if (orderId == null) {
            return null;
        }

        Order order = orderRepository.findById(orderId).map(converter::orderEntityToModel).orElse(null);
        if (order != null) {
            order.setLignes(findLignes(orderId));
        }
        return order;
    }

    @Override
    public List<OrderLine> findLignes(Integer orderId) {
        List<OrderLine> lignes = new ArrayList<>();
        if (orderId == null) {
            return lignes;
        }
        for (OrderLineEntity entity : orderLineRepository.findByOrderId(orderId)) {
            lignes.add(converter.orderLineEntityToModel(entity));
        }
        return lignes;
    }

    @Override
    public List<Order> findByUserId(Integer userId) {
        List<Order> orders = new ArrayList<>();
        if (userId == null) {
            return orders;
        }
        for (OrderEntity entity : orderRepository.findByUserIdOrderByDateCommandeDesc(userId)) {
            orders.add(converter.orderEntityToModel(entity));
        }
        return orders;
    }

    @Override
    public void updateStatut(Integer orderId, String statut, boolean paye) {
        if (orderId == null) {
            return;
        }
        orderRepository.findById(orderId).ifPresent(entity -> {
            entity.setStatut(statut);
            entity.setPaye(paye);
            orderRepository.save(entity);
        });
    }
}
