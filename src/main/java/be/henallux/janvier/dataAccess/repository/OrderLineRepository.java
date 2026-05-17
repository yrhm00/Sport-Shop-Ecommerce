package be.henallux.janvier.dataAccess.repository;

import be.henallux.janvier.dataAccess.entity.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLineEntity, Integer> {
    
    // Trouver toutes les lignes d'une commande
    List<OrderLineEntity> findByOrderId(Integer orderId);

    // Supprimer toutes les lignes d'une commande (ex: annulation PayPal)
    void deleteByOrderId(Integer orderId);
}


