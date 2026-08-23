package be.henallux.janvier.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.henallux.janvier.dataAccess.dao.ProductDataAccess;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.Promotion;

@Service
public class ProductService {

    private final ProductDataAccess productDAO;
    private final PromotionService promotionService;

    @Autowired
    public ProductService(ProductDataAccess productDAO, PromotionService promotionService) {
        this.productDAO = productDAO;
        this.promotionService = promotionService;
    }

    public List<Product> getProductsByCategory(Integer categoryId, String langue) {
        return appliquerPromotions(productDAO.findByCategoryId(categoryId, langue));
    }

    public Product getProductById(Integer id, String langue) {
        Product product = productDAO.findById(id, langue);
        if (product == null) {
            return null;
        }
        promotionService.appliquerPromotions(product);
        return product;
    }

    /** Produits couverts par une promotion active (la liste vient de la base). */
    public List<Product> getPromotions(String langue) {
        return appliquerPromotions(productDAO.findEnPromotion(langue, LocalDateTime.now()));
    }

    /** Applique les promotions en ne les chargeant qu'une fois pour toute la liste. */
    private List<Product> appliquerPromotions(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        List<Promotion> promotionsActives = promotionService.getPromotionsActives();
        for (Product product : products) {
            promotionService.appliquerPromotions(product, promotionsActives);
        }
        return products;
    }
}
