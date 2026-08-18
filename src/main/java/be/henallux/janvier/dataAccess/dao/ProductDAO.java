package be.henallux.janvier.dataAccess.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.projection.TranslatedProduct;
import be.henallux.janvier.dataAccess.repository.ProductRepository;
import be.henallux.janvier.dataAccess.repository.ProductSizeRepository;
import be.henallux.janvier.dataAccess.util.ProviderConverter;
import be.henallux.janvier.model.Product;

@Service
@Transactional(readOnly = true)
public class ProductDAO implements ProductDataAccess {

    private final ProductRepository repository;
    private final ProductSizeRepository sizeRepository;
    private final ProviderConverter converter;

    @Autowired
    public ProductDAO(ProductRepository repository, ProductSizeRepository sizeRepository,
                      ProviderConverter converter) {
        this.repository = repository;
        this.sizeRepository = sizeRepository;
        this.converter = converter;
    }

    @Override
    public List<Product> findAll(String langue) {
        return convertir(repository.findAllTraduit(langue));
    }

    @Override
    public Product findById(Integer id, String langue) {
        if (id == null) {
            return null;
        }
        return converter.translatedProductToModel(repository.findByIdTraduit(id, langue));
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId, String langue) {
        if (categoryId == null) {
            return new ArrayList<>();
        }
        return convertir(repository.findByCategoryIdTraduit(categoryId, langue));
    }

    @Override
    public List<Product> findNouveautes(String langue, int limite) {
        return convertir(repository.findNouveautesTraduit(langue, PageRequest.of(0, limite)));
    }

    @Override
    public List<Product> findEnPromotion(String langue, LocalDateTime maintenant) {
        return convertir(repository.findEnPromotionTraduit(langue, maintenant));
    }

    @Override
    @Transactional
    public boolean decrementerStock(Integer productId, String taille, Integer quantite) {
        if (productId == null || quantite == null || quantite <= 0) {
            return false;
        }

        // Si le produit se decline en tailles, c'est le stock de la taille qui fait foi.
        if (taille != null && !taille.isBlank()) {
            if (sizeRepository.decrementerStock(productId, taille, quantite) == 0) {
                return false;
            }
        }

        return repository.decrementerStock(productId, quantite) > 0;
    }

    private List<Product> convertir(List<TranslatedProduct> translatedProducts) {
        List<Product> products = new ArrayList<>();
        for (TranslatedProduct translated : translatedProducts) {
            products.add(converter.translatedProductToModel(translated));
        }
        return products;
    }
}
