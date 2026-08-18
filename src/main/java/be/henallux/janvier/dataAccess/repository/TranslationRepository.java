package be.henallux.janvier.dataAccess.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.henallux.janvier.dataAccess.entity.TranslationEntity;

/**
 * Acces a la table UNIQUE de traduction.
 *
 * En lecture courante, les traductions sont recuperees par jointure directement
 * dans ProductRepository / CategoryRepository. Ce repository sert aux acces
 * unitaires (tests, administration des traductions).
 */
@Repository
public interface TranslationRepository extends JpaRepository<TranslationEntity, Integer> {

    List<TranslationEntity> findByEntityTypeAndEntityId(String entityType, Integer entityId);

    TranslationEntity findByEntityTypeAndEntityIdAndLocaleAndChamp(String entityType, Integer entityId,
                                                                   String locale, String champ);
}
