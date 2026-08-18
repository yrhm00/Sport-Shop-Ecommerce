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
        // La requete renvoie les produits du plus recent au plus ancien :
        // on ne conserve que les premiers.
        List<Product> produits = convertir(repository.findNouveautesTraduit(langue));
        if (produits.size() > limite) {
            return new ArrayList<>(produits.subList(0, limite));
        }
        return produits;
    }

    @Override
    public List<Product> findEnPromotion(String langue, LocalDateTime maintenant) {
        return convertir(repository.findEnPromotionTraduit(langue, maintenant));
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

        ProductEntity produit = repository.findById(productId).orElse(null);
        if (produit == null || produit.getStock() == null || produit.getStock() < quantite) {
            return false;
        }

        // Si le produit se decline en tailles, c'est le stock de la taille qui fait foi.
        ProductSizeEntity variante = null;
        if (taille != null && !taille.isBlank()) {
            variante = sizeRepository.findByProductIdAndTaille(productId, taille);
            if (variante != null && variante.getStock() < quantite) {
                return false;
            }
        }

        if (variante != null) {
            variante.setStock(variante.getStock() - quantite);
            sizeRepository.save(variante);
        }

        produit.setStock(produit.getStock() - quantite);
        repository.save(produit);
        return true;
    }

    private List<Product> convertir(List<TranslatedProduct> translatedProducts) {
        List<Product> products = new ArrayList<>();
        for (TranslatedProduct translated : translatedProducts) {
            products.add(converter.translatedProductToModel(translated));
        }
        return products;
    }
}
