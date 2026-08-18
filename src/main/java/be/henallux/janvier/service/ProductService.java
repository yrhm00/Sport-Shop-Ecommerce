package be.henallux.janvier.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.henallux.janvier.dataAccess.dao.ProductDataAccess;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.Promotion;

@Service
public class ProductService {

    /** Nombre de produits affiches dans la rubrique "Nouveautes". */
    private static final int NOMBRE_NOUVEAUTES = 4;

    private final ProductDataAccess productDAO;
    private final PromotionService promotionService;

    @Autowired
    public ProductService(ProductDataAccess productDAO, PromotionService promotionService) {
        this.productDAO = productDAO;
        this.promotionService = promotionService;
    }

    public List<Product> getAllProducts(String langue) {
        return enrichir(productDAO.findAll(langue), langue);
    }

    public List<Product> getProductsByCategory(Integer categoryId, String langue) {
        return enrichir(productDAO.findByCategoryId(categoryId, langue), langue);
    }

    public Product getProductById(Integer id, String langue) {
        Product product = productDAO.findById(id, langue);
        if (product == null) {
            return null;
        }
        promotionService.appliquerPromotions(product);
        product.setNewArrival(getIdsNouveautes(langue).contains(product.getId()));
        return product;
    }

    public List<Product> getNewArrivals(String langue) {
        return enrichir(productDAO.findNouveautes(langue, NOMBRE_NOUVEAUTES), langue);
    }

    /** Produits couverts par une promotion active (la liste vient de la base). */
    public List<Product> getPromotions(String langue) {
        return enrichir(productDAO.findEnPromotion(langue, LocalDateTime.now()), langue);
    }

    /**
     * Applique les promotions et marque les nouveautes sur une liste de produits.
     * Les promotions et la liste des nouveautes ne sont chargees qu'une seule fois.
     */
    private List<Product> enrichir(List<Product> products, String langue) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        List<Promotion> promotionsActives = promotionService.getPromotionsActives();
        Set<Integer> idsNouveautes = getIdsNouveautes(langue);

        for (Product product : products) {
            promotionService.appliquerPromotions(product, promotionsActives);
            product.setNewArrival(idsNouveautes.contains(product.getId()));
        }
        return products;
    }

    private Set<Integer> getIdsNouveautes(String langue) {
        List<Product> nouveautes = productDAO.findNouveautes(langue, NOMBRE_NOUVEAUTES);
        if (nouveautes == null) {
            return new HashSet<>();
        }
        return nouveautes.stream().map(Product::getId).collect(Collectors.toSet());
    }
}
