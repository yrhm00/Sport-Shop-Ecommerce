package be.henallux.janvier.dataAccess.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import be.henallux.janvier.dataAccess.entity.AuthorityEntity;
import be.henallux.janvier.dataAccess.entity.CategoryEntity;
import be.henallux.janvier.dataAccess.entity.OrderEntity;
import be.henallux.janvier.dataAccess.entity.OrderLineEntity;
import be.henallux.janvier.dataAccess.entity.ProductEntity;
import be.henallux.janvier.dataAccess.entity.ProductSizeEntity;
import be.henallux.janvier.dataAccess.entity.PromotionEntity;
import be.henallux.janvier.dataAccess.entity.UserEntity;
import be.henallux.janvier.dataAccess.projection.TranslatedCategory;
import be.henallux.janvier.dataAccess.projection.TranslatedProduct;
import be.henallux.janvier.model.Authority;
import be.henallux.janvier.model.Category;
import be.henallux.janvier.model.Order;
import be.henallux.janvier.model.OrderLine;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.model.Promotion;
import be.henallux.janvier.model.User;

/**
 * Conversion entites (couche acces aux donnees) <-> modeles (couche metier).
 *
 * Les traductions ne sont plus filtrees ici : elles arrivent deja resolues par
 * la jointure SQL, via les projections TranslatedProduct / TranslatedCategory.
 */
@Component
public class ProviderConverter {

    private final Mapper mapper;

    public ProviderConverter() {
        this.mapper = DozerBeanMapperBuilder.buildDefault();
    }

    // ========== USER ==========

    public User userEntityToModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        User model = mapper.map(entity, User.class);

        Set<Authority> authorities = new HashSet<>();
        if (entity.getAuthorities() != null) {
            for (AuthorityEntity authEntity : entity.getAuthorities()) {
                authorities.add(new Authority(authEntity.getAuthority()));
            }
        }
        model.setAuthorities(authorities);

        return model;
    }

    public UserEntity userModelToEntity(User model) {
        if (model == null) {
            return null;
        }
        UserEntity entity = mapper.map(model, UserEntity.class);
        // Les autorites sont gerees separement par le DAO.
        entity.setAuthorities(null);
        return entity;
    }

    public Authority authorityEntityToModel(AuthorityEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Authority(entity.getAuthority());
    }

    public AuthorityEntity authorityModelToEntity(Authority model, String username) {
        if (model == null) {
            return null;
        }
        return new AuthorityEntity(username, model.getAuthority());
    }

    // ========== CATEGORY ==========

    public Category categoryEntityToModel(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapper.map(entity, Category.class);
    }

    /** Convertit une categorie deja jointe a sa traduction. */
    public Category translatedCategoryToModel(TranslatedCategory translated) {
        if (translated == null || translated.getCategory() == null) {
            return null;
        }

        Category model = categoryEntityToModel(translated.getCategory());
        if (translated.getNomTraduit() != null) {
            model.setNom(translated.getNomTraduit());
        }
        return model;
    }

    public CategoryEntity categoryModelToEntity(Category model) {
        if (model == null) {
            return null;
        }
        return mapper.map(model, CategoryEntity.class);
    }

    // ========== PRODUCT ==========

    public Product productEntityToModel(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        Product model = mapper.map(entity, Product.class);

        if (entity.getCategory() != null) {
            model.setCategoryNom(entity.getCategory().getNom());
        }

        // Stock par taille : somme des variantes si le produit en possede.
        if (entity.getSizes() != null && !entity.getSizes().isEmpty()) {
            Map<String, Integer> sizesStock = new HashMap<>();
            int totalStock = 0;
            for (ProductSizeEntity sizeEntity : entity.getSizes()) {
                sizesStock.put(sizeEntity.getTaille(), sizeEntity.getStock());
                totalStock += sizeEntity.getStock();
            }
            model.setSizesStock(sizesStock);
            model.setStock(totalStock);
        }

        return model;
    }

    /** Convertit un produit deja joint a ses traductions. */
    public Product translatedProductToModel(TranslatedProduct translated) {
        if (translated == null || translated.getProduct() == null) {
            return null;
        }

        Product model = productEntityToModel(translated.getProduct());
        if (translated.getNomTraduit() != null) {
            model.setNom(translated.getNomTraduit());
        }
        if (translated.getDescriptionTraduite() != null) {
            model.setDescription(translated.getDescriptionTraduite());
        }
        return model;
    }

    public ProductEntity productModelToEntity(Product model) {
        if (model == null) {
            return null;
        }
        return mapper.map(model, ProductEntity.class);
    }

    // ========== PROMOTION ==========

    public Promotion promotionEntityToModel(PromotionEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapper.map(entity, Promotion.class);
    }

    // ========== ORDER ==========

    public Order orderEntityToModel(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        Order model = new Order();
        model.setId(entity.getId());
        model.setUserId(entity.getUserId());
        model.setDateCommande(entity.getDateCommande());
        model.setMontantTotal(entity.getMontantTotal());
        model.setMontantReduction(entity.getMontantReduction());
        model.setPaye(Boolean.TRUE.equals(entity.getPaye()));
        model.setStatut(entity.getStatut());
        return model;
    }

    public OrderLine orderLineEntityToModel(OrderLineEntity entity) {
        if (entity == null) {
            return null;
        }

        OrderLine model = new OrderLine();
        model.setId(entity.getId());
        model.setOrderId(entity.getOrderId());
        model.setProductId(entity.getProductId());
        model.setTaille(entity.getTaille());
        model.setQuantite(entity.getQuantite());
        model.setPrixUnitaire(entity.getPrixUnitaire());
        return model;
    }
}
