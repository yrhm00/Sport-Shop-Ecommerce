package be.henallux.janvier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import be.henallux.janvier.dataAccess.dao.UserDataAccess;
import be.henallux.janvier.model.InscriptionForm;
import be.henallux.janvier.model.User;

class InscriptionServiceTest {

    private InscriptionService inscriptionService;

    @Mock
    private UserDataAccess userDAO;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inscriptionService = new InscriptionService(userDAO, passwordEncoder, new SanitizationService());
    }

    @Test
    void motsDePasseIdentiques() {
        assertTrue(inscriptionService.passwordsMatch("MotDePasse2026!", "MotDePasse2026!"));
    }

    @Test
    void motsDePasseDifferents() {
        assertFalse(inscriptionService.passwordsMatch("MotDePasse2026!", "AutreMotDePasse"));
    }

    @Test
    void motDePasseNull() {
        assertFalse(inscriptionService.passwordsMatch(null, "MotDePasse2026!"));
        assertFalse(inscriptionService.passwordsMatch("MotDePasse2026!", null));
    }

    @Test
    void usernameDejaUtilise() {
        when(userDAO.existsByUsername("existingUser")).thenReturn(true);
        when(userDAO.existsByUsername("newUser")).thenReturn(false);

        assertTrue(inscriptionService.usernameExists("existingUser"));
        assertFalse(inscriptionService.usernameExists("newUser"));
    }

    @Test
    void emailDejaUtilise() {
        when(userDAO.existsByEmail("used@email.be")).thenReturn(true);
        when(userDAO.existsByEmail("free@email.be")).thenReturn(false);

        assertTrue(inscriptionService.emailExists("used@email.be"));
        assertFalse(inscriptionService.emailExists("free@email.be"));
    }

    @Test
    void leMotDePasseEstHacheEtLesChampsSontNettoyes() {
        when(userDAO.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InscriptionForm form = new InscriptionForm();
        form.setUsername("jdupont");
        form.setPassword("MotDePasse2026!");
        form.setConfirmPassword("MotDePasse2026!");
        // Tentative d'injection de script dans un champ libre.
        form.setNom("<script>alert('xss')</script>Dupont");
        form.setPrenom("Jean");
        form.setEmail("jean@email.be");
        form.setTelephone("0470000000");
        form.setAdresse("10 Avenue des Tilleuls");
        form.setCodePostal("4000");
        form.setLocalite("Liege");

        inscriptionService.createUser(form);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userDAO).save(captor.capture());
        User enregistre = captor.getValue();

        // Le mot de passe n'est jamais stocke en clair.
        assertNotEquals("MotDePasse2026!", enregistre.getPassword());
        assertTrue(passwordEncoder.matches("MotDePasse2026!", enregistre.getPassword()));

        // Le balisage HTML a ete retire de la saisie.
        assertEquals("Dupont", enregistre.getNom());
        assertTrue(enregistre.isEnabled());
    }

    @Test
    void lesAutoritesParDefautSontRoleUser() {
        when(userDAO.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userDAO.existsByUsername(anyString())).thenReturn(false);

        InscriptionForm form = new InscriptionForm();
        form.setUsername("nouveau");
        form.setPassword("MotDePasse2026!");
        form.setNom("Nom");
        form.setPrenom("Prenom");
        form.setEmail("nouveau@email.be");
        form.setAdresse("1 rue de la Paix");
        form.setCodePostal("5000");
        form.setLocalite("Namur");

        User cree = inscriptionService.createUser(form);

        assertEquals(1, cree.getAuthorities().size());
        assertEquals("ROLE_USER", cree.getAuthorities().iterator().next().getAuthority());
    }
}
