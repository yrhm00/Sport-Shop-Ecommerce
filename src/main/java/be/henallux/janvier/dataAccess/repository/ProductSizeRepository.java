package be.henallux.janvier.dataAccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.ProductSizeEntity;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSizeEntity, Integer> {

    ProductSizeEntity findByProductIdAndTaille(Integer productId, String taille);
}
