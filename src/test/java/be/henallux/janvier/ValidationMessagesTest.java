package be.henallux.janvier;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

class ValidationMessagesTest {

    private ResourceBundleMessageSource messageSource;

    @BeforeEach
    void setUp() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
    }

    @Test
    void afficheLesBornesEtLapostropheDuNomUtilisateur() {
        String message = messageSource.getMessage(
                "Size.inscriptionForm.username",
                new Object[] {"Nom d'utilisateur", 50, 2},
                Locale.FRENCH);

        assertEquals(
                "Le nom d'utilisateur doit contenir entre 2 et 50 caractères.",
                message);
    }

    @Test
    void afficheLesBornesEtLapostropheDeLadresse() {
        String message = messageSource.getMessage(
                "Size.inscriptionForm.adresse",
                new Object[] {"Adresse", 500, 10},
                Locale.FRENCH);

        assertEquals(
                "L'adresse doit contenir entre 10 et 500 caractères.",
                message);
    }

    @Test
    void afficheLapostropheDansLeMessageObligatoire() {
        String message = messageSource.getMessage(
                "NotBlank.inscriptionForm.username",
                new Object[] {"Nom d'utilisateur"},
                Locale.FRENCH);

        assertEquals("Le nom d'utilisateur est obligatoire.", message);
    }
}
