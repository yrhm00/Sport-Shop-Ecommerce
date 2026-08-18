package be.henallux.janvier.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import be.henallux.janvier.model.Category;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.service.CategoryService;
import be.henallux.janvier.service.ProductService;

@Controller
@RequestMapping(value = "/produits")
public class ProduitController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public ProduitController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    /**
     * Affiche la liste des categories.
     * La langue courante est fournie par Spring MVC et transmise a la couche metier.
     */
    @GetMapping
    public String showCategories(Model model, Locale locale) {
        List<Category> categories = categoryService.getAllCategories(locale.getLanguage());
        model.addAttribute("categories", categories);
        return "produits";
    }

    /** Affiche les produits d'une categorie. */
    @GetMapping("/categorie/{categoryId}")
    public String showProductsByCategory(@PathVariable Integer categoryId, Model model, Locale locale) {
        Category category = categoryService.getCategoryById(categoryId, locale.getLanguage());

        // La categorie demandee dans l'URL peut ne pas exister : on affiche une
        // page d'erreur traduite plutot que de laisser passer une valeur nulle.
        if (category == null) {
            model.addAttribute("cleErreur", "error.category.notFound");
            return "erreur";
        }

        model.addAttribute("category", category);
        model.addAttribute("products", productService.getProductsByCategory(categoryId, locale.getLanguage()));
        return "produits-liste";
    }

    /** Affiche le detail d'un produit. */
    @GetMapping("/{productId}")
    public String showProductDetails(@PathVariable Integer productId, Model model, Locale locale) {
        Product product = productService.getProductById(productId, locale.getLanguage());

        if (product == null) {
            model.addAttribute("cleErreur", "error.product.notFound");
            return "erreur";
        }

        model.addAttribute("product", product);
        return "produit-detail";
    }

    @GetMapping("/nouveautes")
    public String showNewArrivals(Model model, Locale locale) {
        model.addAttribute("titreRubrique", "products.newArrivals");
        model.addAttribute("products", productService.getNewArrivals(locale.getLanguage()));
        return "produits-liste";
    }

    @GetMapping("/promotions")
    public String showPromotions(Model model, Locale locale) {
        model.addAttribute("titreRubrique", "products.promotions");
        model.addAttribute("products", productService.getPromotions(locale.getLanguage()));
        return "produits-liste";
    }
}
