package be.henallux.janvier.dataAccess.dao;

import java.time.LocalDateTime;
import java.util.List;

import be.henallux.janvier.model.Promotion;

public interface PromotionDataAccess {

    /** Promotions actives et valides a l'instant donne. */
    List<Promotion> findPromotionsActives(LocalDateTime maintenant);
}
