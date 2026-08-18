package be.henallux.janvier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests du nettoyage anti-XSS des saisies utilisateur. */
class SanitizationServiceTest {

    private SanitizationService sanitizationService;

    @BeforeEach
    void setUp() {
        sanitizationService = new SanitizationService();
    }

    @Test
    void leTexteSimpleEstConserve() {
        assertEquals("Jean Dupont", sanitizationService.nettoyer("Jean Dupont"));
    }

    @Test
    void leScriptEstSupprime() {
        String nettoye = sanitizationService.nettoyer("<script>alert('xss')</script>Dupont");
        assertEquals("Dupont", nettoye);
        assertFalse(nettoye.contains("<"));
    }

    @Test
    void lesBalisesHtmlSontSupprimees() {
        assertEquals("gras", sanitizationService.nettoyer("<b>gras</b>"));
        assertEquals("lien", sanitizationService.nettoyer("<a href=\"http://exemple.be\">lien</a>"));
    }

    @Test
    void lesEspacesSuperflusSontRetires() {
        assertEquals("Namur", sanitizationService.nettoyer("   Namur   "));
    }

    @Test
    void nullResteNull() {
        assertNull(sanitizationService.nettoyer(null));
    }
}
