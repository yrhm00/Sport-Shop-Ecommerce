package be.henallux.janvier.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.henallux.janvier.dataAccess.dao.CategoryDataAccess;
import be.henallux.janvier.model.Category;

@Service
public class CategoryService {

    private final CategoryDataAccess categoryDAO;

    @Autowired
    public CategoryService(CategoryDataAccess categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories(String langue) {
        return categoryDAO.findAll(langue);
    }

    public Category getCategoryById(Integer id, String langue) {
        return categoryDAO.findById(id, langue);
    }

}
