package be.henallux.janvier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import be.henallux.janvier.model.Cart;
import be.henallux.janvier.model.Product;
import be.henallux.janvier.service.ProductService;
import be.henallux.janvier.service.PromotionService;

class PanierControllerTest {

    private PanierController controller;
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private PromotionService promotionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PanierController(productService, promotionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void tailleInconnueNEstPasAjouteeAuPanier() {
        Product product = produitAvecTailles();
        when(productService.getProductById(1, "fr")).thenReturn(product);
        MockHttpSession session = new MockHttpSession();

        String redirection = controller.addToCart(1, "1", "99", session, Locale.FRENCH);

        assertEquals("redirect:/produits/1?erreur=taille", redirection);
        assertNull(session.getAttribute("cart"));
    }

    @Test
    void tailleAbsenteEstRefuseePourUnProduitAvecTailles() {
        Product product = produitAvecTailles();
        when(productService.getProductById(1, "fr")).thenReturn(product);

        String redirection = controller.addToCart(1, "1", null,
                new MockHttpSession(), Locale.FRENCH);

        assertEquals("redirect:/produits/1?erreur=taille", redirection);
    }

    @Test
    void tailleInjecteeEstRefuseePourUnProduitSansTailles() {
        Product product = new Product();
        product.setId(2);
        product.setStock(10);
        when(productService.getProductById(2, "fr")).thenReturn(product);

        String redirection = controller.addToCart(2, "1", "TAILLE_INVALIDE",
                new MockHttpSession(), Locale.FRENCH);

        assertEquals("redirect:/produits/2?erreur=taille", redirection);
    }

    @Test
    void plusieursAjoutsNePeuventPasDepasserLeStockDeLaTaille() {
        Product product = produitAvecTailles();
        when(productService.getProductById(1, "fr")).thenReturn(product);
        MockHttpSession session = new MockHttpSession();

        assertEquals("redirect:/panier?ajoute",
                controller.addToCart(1, "3", "42", session, Locale.FRENCH));
        assertEquals("redirect:/produits/1?erreur=stock",
                controller.addToCart(1, "3", "42", session, Locale.FRENCH));

        Cart cart = (Cart) session.getAttribute("cart");
        assertNotNull(cart);
        assertEquals(3, cart.getQuantity(1, "42"));
    }

    @Test
    void quantiteVideAfficheUneErreurDuPanierAuLieuDUnePage400() throws Exception {
        mockMvc.perform(post("/panier/modifier")
                        .param("productId", "1")
                        .param("quantite", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/panier?erreur=quantite"));
    }

    @Test
    void quantiteNonNumeriqueAfficheUneErreurDuPanierAuLieuDUnePage400() throws Exception {
        mockMvc.perform(post("/panier/modifier")
                        .param("productId", "1")
                        .param("quantite", "abc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/panier?erreur=quantite"));
    }

    @Test
    void quantiteVideALAjoutAfficheUneErreurAuLieuDUnePage400() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setStock(10);
        when(productService.getProductById(2, "fr")).thenReturn(product);

        mockMvc.perform(post("/panier/ajouter/2")
                        .locale(Locale.FRENCH)
                        .param("quantite", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produits/2?erreur=quantite"));
    }

    private Product produitAvecTailles() {
        Product product = new Product();
        product.setId(1);
        product.setStock(5);
        HashMap<String, Integer> tailles = new HashMap<>();
        tailles.put("42", 5);
        product.setSizesStock(tailles);
        return product;
    }
}
