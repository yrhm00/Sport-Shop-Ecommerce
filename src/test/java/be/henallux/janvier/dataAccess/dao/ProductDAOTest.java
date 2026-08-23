package be.henallux.janvier.dataAccess.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.henallux.janvier.dataAccess.entity.ProductEntity;
import be.henallux.janvier.dataAccess.entity.ProductSizeEntity;
import be.henallux.janvier.dataAccess.repository.ProductRepository;
import be.henallux.janvier.dataAccess.repository.ProductSizeRepository;
import be.henallux.janvier.dataAccess.util.ProviderConverter;

/**
 * Tests du retrait de stock : le stock ne doit jamais devenir negatif.
 */
class ProductDAOTest {

    private ProductDAO productDAO;

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductSizeRepository sizeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDAO = new ProductDAO(repository, sizeRepository, new ProviderConverter());
    }

    private ProductEntity produit(int stock) {
        ProductEntity entity = new ProductEntity();
        entity.setId(1);
        entity.setStock(stock);
        return entity;
    }

    @Test
    void leStockEstRetireQuandIlEstSuffisant() {
        ProductEntity entity = produit(10);
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(entity));

        assertTrue(productDAO.decrementerStock(1, null, 3));

        assertEquals(7, entity.getStock());
        verify(repository).save(entity);
    }

    @Test
    void leStockDeLaTailleEstRetireAussi() {
        ProductEntity entity = produit(10);
        ProductSizeEntity variante = new ProductSizeEntity(1, "42", 4);
        entity.getSizes().add(variante);
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(entity));
        when(sizeRepository.findByProductIdAndTaille(1, "42")).thenReturn(variante);

        assertTrue(productDAO.decrementerStock(1, "42", 2));

        assertEquals(2, variante.getStock());
        assertEquals(8, entity.getStock());
        verify(sizeRepository).save(variante);
    }

    @Test
    void rienNEstModifieSiLeStockGlobalEstInsuffisant() {
        ProductEntity entity = produit(2);
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(entity));

        assertFalse(productDAO.decrementerStock(1, null, 5));

        assertEquals(2, entity.getStock());
        verify(repository, never()).save(any(ProductEntity.class));
    }

    @Test
    void rienNEstModifieSiLeStockDeLaTailleEstInsuffisant() {
        ProductEntity entity = produit(50);
        ProductSizeEntity variante = new ProductSizeEntity(1, "42", 1);
        entity.getSizes().add(variante);
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(entity));
        when(sizeRepository.findByProductIdAndTaille(1, "42")).thenReturn(variante);

        assertFalse(productDAO.decrementerStock(1, "42", 3));

        assertEquals(1, variante.getStock());
        assertEquals(50, entity.getStock());
        verify(repository, never()).save(any(ProductEntity.class));
        verify(sizeRepository, never()).save(any(ProductSizeEntity.class));
    }

    @Test
    void tailleInconnueOuAbsenteEstRefuseePourUnProduitAvecTailles() {
        ProductEntity entity = produit(10);
        entity.getSizes().add(new ProductSizeEntity(1, "42", 4));
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(entity));
        when(sizeRepository.findByProductIdAndTaille(1, "99")).thenReturn(null);

        assertFalse(productDAO.decrementerStock(1, "99", 1));
        assertFalse(productDAO.decrementerStock(1, null, 1));

        assertEquals(10, entity.getStock());
        verify(repository, never()).save(any(ProductEntity.class));
        verify(sizeRepository, never()).save(any(ProductSizeEntity.class));
    }

    @Test
    void tailleInjecteeEstRefuseePourUnProduitSansTailles() {
        ProductEntity entity = produit(10);
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(entity));

        assertFalse(productDAO.decrementerStock(1, "TAILLE_INVALIDE", 1));

        assertEquals(10, entity.getStock());
        verify(repository, never()).save(any(ProductEntity.class));
    }

    @Test
    void produitInexistantOuQuantiteInvalide() {
        when(repository.findByIdForUpdate(99)).thenReturn(Optional.empty());

        assertFalse(productDAO.decrementerStock(99, null, 1));
        assertFalse(productDAO.decrementerStock(null, null, 1));
        assertFalse(productDAO.decrementerStock(1, null, 0));
        assertFalse(productDAO.decrementerStock(1, null, -5));

        verify(repository, never()).save(any(ProductEntity.class));
    }

    @Test
    void laVerificationDuStockNeLeModifiePas() {
        ProductEntity entity = produit(10);
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        assertTrue(productDAO.stockSuffisant(1, null, 4));
        assertFalse(productDAO.stockSuffisant(1, null, 11));

        assertEquals(10, entity.getStock());
        verify(repository, never()).save(any(ProductEntity.class));
    }
}
