package be.henallux.janvier.dataAccess.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.entity.ProductEntity;
import be.henallux.janvier.dataAccess.entity.ProductSizeEntity;
import be.henallux.janvier.dataAccess.projection.TranslatedProduct;
import be.henallux.janvier.dataAccess.repository.ProductRepository;
import be.henallux.janvier.dataAccess.repository.ProductSizeRepository;
import be.henallux.janvier.dataAccess.util.ProviderConverter;
import be.henallux.janvier.model.Product;

@Service
@Transactional
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
    public List<Product> findEnPromotion(String langue, LocalDateTime maintenant) {
        return convertir(repository.findEnPromotionTraduit(langue, maintenant));
    }

    /**
     * Verifie le stock global et, lorsque le produit se decline en tailles, le
     * stock de la variante demandee.
     */
    @Override
    public boolean stockSuffisant(Integer productId, String taille, Integer quantite) {
        if (productId == null || quantite == null || quantite <= 0) {
            return false;
        }
        ProductEntity produit = repository.findById(productId).orElse(null);
        return stockSuffisant(produit, taille, quantite);
    }

    /**
     * Retire la quantite commandee du stock.
     *
     * Les entites sont chargees puis modifiees : Hibernate ecrit la mise a jour
     * a la fin de la transaction. Le stock n'est jamais rendu negatif.
     */
    @Override
    public boolean decrementerStock(Integer productId, String taille, Integer quantite) {
        if (productId == null || quantite == null || quantite <= 0) {
            return false;
        }

        ProductEntity produit = repository.findByIdForUpdate(productId).orElse(null);
        if (!stockSuffisant(produit, taille, quantite)) {
            return false;
        }

        // Si le produit se decline en tailles, une variante existante est
        // obligatoire. Sinon toute taille recue est une valeur injectee.
        ProductSizeEntity variante = null;
        boolean produitAvecTailles = produit.getSizes() != null && !produit.getSizes().isEmpty();
        if (produitAvecTailles) {
            variante = sizeRepository.findByProductIdAndTaille(productId, taille);
        }

        if (variante != null) {
            variante.setStock(variante.getStock() - quantite);
            sizeRepository.save(variante);
        }

        produit.setStock(produit.getStock() - quantite);
        repository.save(produit);
        return true;
    }

    private boolean stockSuffisant(ProductEntity produit, String taille, Integer quantite) {
        if (produit == null || quantite == null || quantite <= 0
                || produit.getStock() == null || produit.getStock() < quantite) {
            return false;
        }

        boolean produitAvecTailles = produit.getSizes() != null && !produit.getSizes().isEmpty();
        if (!produitAvecTailles) {
            return taille == null || taille.isBlank();
        }
        if (taille == null || taille.isBlank()) {
            return false;
        }

        ProductSizeEntity variante = sizeRepository.findByProductIdAndTaille(produit.getId(), taille);
        return variante != null && variante.getStock() != null && variante.getStock() >= quantite;
    }

    private List<Product> convertir(List<TranslatedProduct> translatedProducts) {
        List<Product> products = new ArrayList<>();
        for (TranslatedProduct translated : translatedProducts) {
            products.add(converter.translatedProductToModel(translated));
        }
        return products;
    }
}
