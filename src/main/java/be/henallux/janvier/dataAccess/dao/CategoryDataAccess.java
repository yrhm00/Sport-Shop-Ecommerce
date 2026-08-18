package be.henallux.janvier.dataAccess.dao;

import java.util.List;

import be.henallux.janvier.model.Category;

/**
 * Interface d'acces aux categories.
 * La langue est un parametre : la couche d'acces aux donnees ne connait pas le web.
 */
public interface CategoryDataAccess {

    List<Category> findAll(String langue);

    Category findById(Integer id, String langue);

    Category findByCode(String code, String langue);
}
