package be.henallux.janvier.dataAccess.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.PromotionEntity;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Integer> {

    PromotionEntity findByCode(String code);

    /**
     * Promotions actives a l'instant donne : le drapeau 'active' est vrai et la
     * date du jour est comprise dans la periode de validite (bornes facultatives).
     */
    @Query("select p from PromotionEntity p "
         + "where p.active = true "
         + "and (p.dateDebut is null or p.dateDebut <= :maintenant) "
         + "and (p.dateFin is null or p.dateFin >= :maintenant)")
    List<PromotionEntity> findPromotionsActives(@Param("maintenant") LocalDateTime maintenant);
}
