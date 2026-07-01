package be.henallux.janvier.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import be.henallux.janvier.dataAccess.dao.UserDAO;

public class InscriptionServiceTest {

    private InscriptionService inscriptionService;

    @Mock
    private UserDAO userDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        inscriptionService = new InscriptionService(userDAO, passwordEncoder);
    }

    @Test
    public void testPasswordsMatchSuccess() {
        assertTrue(inscriptionService.passwordsMatch("password123", "password123"));
    }

    @Test
    public void testPasswordsMatchFailure() {
        assertFalse(inscriptionService.passwordsMatch("password123", "differentPassword"));
    }

    @Test
    public void testPasswordsMatchNull() {
        assertFalse(inscriptionService.passwordsMatch(null, "password123"));
        assertFalse(inscriptionService.passwordsMatch("password123", null));
    }

    @Test
    public void testUsernameExists() {
        when(userDAO.existsByUsername("existingUser")).thenReturn(true);
        when(userDAO.existsByUsername("newUser")).thenReturn(false);

        assertTrue(inscriptionService.usernameExists("existingUser"));
        assertFalse(inscriptionService.usernameExists("newUser"));
    }

    @Test
    public void testEmailExists() {
        when(userDAO.existsByEmail("used@email.be")).thenReturn(true);
        when(userDAO.existsByEmail("free@email.be")).thenReturn(false);

        assertTrue(inscriptionService.emailExists("used@email.be"));
        assertFalse(inscriptionService.emailExists("free@email.be"));
    }
}
