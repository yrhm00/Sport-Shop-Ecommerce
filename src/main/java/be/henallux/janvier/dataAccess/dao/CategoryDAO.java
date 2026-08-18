package be.henallux.janvier.dataAccess.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.projection.TranslatedCategory;
import be.henallux.janvier.dataAccess.repository.CategoryRepository;
import be.henallux.janvier.dataAccess.util.ProviderConverter;
import be.henallux.janvier.model.Category;

@Service
@Transactional(readOnly = true)
public class CategoryDAO implements CategoryDataAccess {

    private final CategoryRepository repository;
    private final ProviderConverter converter;

    @Autowired
    public CategoryDAO(CategoryRepository repository, ProviderConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public List<Category> findAll(String langue) {
        List<Category> categories = new ArrayList<>();
        for (TranslatedCategory translated : repository.findAllTraduit(langue)) {
            categories.add(converter.translatedCategoryToModel(translated));
        }
        return categories;
    }

    @Override
    public Category findById(Integer id, String langue) {
        if (id == null) {
            return null;
        }
        return converter.translatedCategoryToModel(repository.findByIdTraduit(id, langue));
    }

    @Override
    public Category findByCode(String code, String langue) {
        if (code == null) {
            return null;
        }
        return converter.translatedCategoryToModel(repository.findByCodeTraduit(code, langue));
    }
}
