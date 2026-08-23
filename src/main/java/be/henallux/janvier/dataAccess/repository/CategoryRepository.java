package be.henallux.janvier.dataAccess.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.CategoryEntity;
import be.henallux.janvier.dataAccess.projection.TranslatedCategory;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    /**
     * Meme principe que ProductRepository : le nom traduit est ramene par
     * JOINTURE avec la table unique 'translations', filtree sur la langue.
     */
    String SELECT_TRADUIT =
          "select new be.henallux.janvier.dataAccess.projection.TranslatedCategory(c, t.valeur) "
        + "from CategoryEntity c "
        + "left join TranslationEntity t "
        + "       on t.entityType = 'CATEGORY' and t.entityId = c.id "
        + "      and t.locale = :locale and t.champ = 'nom' ";

    @Query(SELECT_TRADUIT + "order by c.nom asc")
    List<TranslatedCategory> findAllTraduit(@Param("locale") String locale);

    @Query(SELECT_TRADUIT + "where c.id = :id")
    TranslatedCategory findByIdTraduit(@Param("id") Integer id, @Param("locale") String locale);

}
