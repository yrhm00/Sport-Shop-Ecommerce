package be.henallux.janvier.dataAccess.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.ProductEntity;
import be.henallux.janvier.dataAccess.projection.TranslatedProduct;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    /** Verrou utilise pendant le retrait de stock pour eviter deux ventes simultanees. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id = :id")
    Optional<ProductEntity> findByIdForUpdate(@Param("id") Integer id);

    /**
     * Fragment de jointure commun a toutes les requetes traduites.
     *
     * Le nom et la description sont ramenes par une JOINTURE avec la table unique
     * 'translations' : une jointure par champ traduit, filtree sur la langue
     * demandee. Aucun filtrage de langue n'est fait en Java.
     */
    String SELECT_TRADUIT =
          "select new be.henallux.janvier.dataAccess.projection.TranslatedProduct(p, tn.valeur, td.valeur, tc.valeur) "
        + "from ProductEntity p "
        + "left join TranslationEntity tn "
        + "       on tn.entityType = 'PRODUCT' and tn.entityId = p.id "
        + "      and tn.locale = :locale and tn.champ = 'nom' "
        + "left join TranslationEntity td "
        + "       on td.entityType = 'PRODUCT' and td.entityId = p.id "
        + "      and td.locale = :locale and td.champ = 'description' "
        + "left join TranslationEntity tc "
        + "       on tc.entityType = 'CATEGORY' and tc.entityId = p.categoryId "
        + "      and tc.locale = :locale and tc.champ = 'nom' ";

    @Query(SELECT_TRADUIT + "where p.categoryId = :categoryId order by p.nom asc")
    List<TranslatedProduct> findByCategoryIdTraduit(@Param("categoryId") Integer categoryId,
                                                    @Param("locale") String locale);

    @Query(SELECT_TRADUIT + "where p.id = :id")
    TranslatedProduct findByIdTraduit(@Param("id") Integer id, @Param("locale") String locale);

    /**
     * Produits couverts par une promotion active : soit une promotion ciblant
     * directement le produit, soit une promotion ciblant sa categorie.
     * La liste vient donc de la table 'promotions', rien n'est code en dur.
     */
    @Query(SELECT_TRADUIT
         + "where exists (select 1 from PromotionEntity pr "
         + "              where pr.active = true "
         + "                and (pr.dateDebut is null or pr.dateDebut <= :maintenant) "
         + "                and (pr.dateFin is null or pr.dateFin >= :maintenant) "
         + "                and ((pr.portee = 'PRODUIT' and pr.productId = p.id) "
         + "                  or (pr.portee = 'CATEGORIE' and pr.categoryId = p.categoryId))) "
         + "order by p.nom asc")
    List<TranslatedProduct> findEnPromotionTraduit(@Param("locale") String locale,
                                                   @Param("maintenant") LocalDateTime maintenant);

}
