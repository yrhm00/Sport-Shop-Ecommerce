package be.henallux.janvier.dataAccess.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    List<OrderEntity> findByUserIdOrderByDateCommandeDesc(Integer userId);

    List<OrderEntity> findByUserIdAndStatutOrderByDateCommandeDesc(Integer userId, String statut);

    List<OrderEntity> findByPaye(Boolean paye);
}
