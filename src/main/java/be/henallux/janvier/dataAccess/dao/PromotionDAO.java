package be.henallux.janvier.dataAccess.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.entity.PromotionEntity;
import be.henallux.janvier.dataAccess.repository.PromotionRepository;
import be.henallux.janvier.dataAccess.util.ProviderConverter;
import be.henallux.janvier.model.Promotion;

@Service
@Transactional(readOnly = true)
public class PromotionDAO implements PromotionDataAccess {

    private final PromotionRepository repository;
    private final ProviderConverter converter;

    @Autowired
    public PromotionDAO(PromotionRepository repository, ProviderConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public List<Promotion> findPromotionsActives(LocalDateTime maintenant) {
        List<Promotion> promotions = new ArrayList<>();
        for (PromotionEntity entity : repository.findPromotionsActives(maintenant)) {
            promotions.add(converter.promotionEntityToModel(entity));
        }
        return promotions;
    }
}
