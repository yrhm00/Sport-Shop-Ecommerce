package be.henallux.janvier.dataAccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.ProductSizeEntity;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSizeEntity, Integer> {

    ProductSizeEntity findByProductIdAndTaille(Integer productId, String taille);

    /**
     * Decremente le stock d'une taille precise, sans jamais passer sous zero.
     */
    @Modifying
    @Query("update ProductSizeEntity s set s.stock = s.stock - :quantite "
         + "where s.productId = :productId and s.taille = :taille and s.stock >= :quantite")
    int decrementerStock(@Param("productId") Integer productId, @Param("taille") String taille,
                         @Param("quantite") Integer quantite);
}
