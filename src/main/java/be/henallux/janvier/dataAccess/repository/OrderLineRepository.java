package be.henallux.janvier.dataAccess.repository;

import be.henallux.janvier.dataAccess.entity.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLineEntity, Integer> {

    List<OrderLineEntity> findByOrderId(Integer orderId);
}

