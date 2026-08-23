package be.henallux.janvier.dataAccess.dao;

import java.time.LocalDateTime;
import java.util.List;

import be.henallux.janvier.model.Product;

public interface ProductDataAccess {

    Product findById(Integer id, String langue);

    List<Product> findByCategoryId(Integer categoryId, String langue);

    List<Product> findEnPromotion(String langue, LocalDateTime maintenant);

    /** Verifie le stock courant sans le modifier. */
    boolean stockSuffisant(Integer productId, String taille, Integer quantite);

    /**
     * Retire la quantite du stock (stock global et stock de la taille si fournie).
     * Renvoie false si le stock disponible est insuffisant : rien n'est alors modifie.
     */
    boolean decrementerStock(Integer productId, String taille, Integer quantite);
}
