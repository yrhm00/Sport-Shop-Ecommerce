package be.henallux.janvier.dataAccess.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import be.henallux.janvier.dataAccess.entity.CategoryEntity;
import be.henallux.janvier.dataAccess.entity.OrderEntity;
import be.henallux.janvier.dataAccess.entity.ProductEntity;
import be.henallux.janvier.dataAccess.projection.TranslatedProduct;
import be.henallux.janvier.model.Order;
import be.henallux.janvier.model.Product;

class ProviderConverterTest {

    @Test
    void leNomTraduitDeLaCategorieEstUtiliseSurLeProduit() {
        CategoryEntity category = new CategoryEntity("SHOES", "Chaussures", null);
        ProductEntity entity = new ProductEntity();
        entity.setCategory(category);

        TranslatedProduct translated = new TranslatedProduct(
                entity, "Running shoe", "English description", "Running shoes");

        Product product = new ProviderConverter().translatedProductToModel(translated);

        assertEquals("Running shoe", product.getNom());
        assertEquals("English description", product.getDescription());
        assertEquals("Running shoes", product.getCategoryNom());
    }

    @Test
    void identifiantPaypalEstConservePendantLaConversion() {
        OrderEntity entity = new OrderEntity();
        entity.setPaypalOrderId("PAYPAL-42");

        Order order = new ProviderConverter().orderEntityToModel(entity);

        assertEquals("PAYPAL-42", order.getPaypalOrderId());
    }
}
