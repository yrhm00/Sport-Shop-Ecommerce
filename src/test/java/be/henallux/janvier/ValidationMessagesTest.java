package be.henallux.janvier;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import be.henallux.janvier.configuration.WebConfiguration;
import be.henallux.janvier.model.InscriptionForm;
import be.henallux.janvier.model.ProfileForm;

class ValidationMessagesTest {

    private MessageSource messageSource;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        WebConfiguration configuration = new WebConfiguration();
        messageSource = configuration.messageSource();
        validator = configuration.validator();
        validator.afterPropertiesSet();
    }

    @Test
    void afficheLesBornesEtLapostropheDuNomUtilisateur() {
        InscriptionForm form = new InscriptionForm();
        form.setUsername("x");
        String message = messageDeValidation(form, "inscriptionForm",
                "username", "Size", Locale.FRENCH);

        assertEquals(
                "Le nom d'utilisateur doit contenir entre 2 et 50 caractères.",
                message);
    }

    @Test
    void afficheLesBornesEtLapostropheDeLadresse() {
        InscriptionForm form = new InscriptionForm();
        form.setAdresse("x");
        String message = messageDeValidation(form, "inscriptionForm",
                "adresse", "Size", Locale.FRENCH);

        assertEquals(
                "L'adresse doit contenir entre 10 et 500 caractères.",
                message);
    }

    @Test
    void afficheLapostropheDansLeMessageObligatoire() {
        InscriptionForm form = new InscriptionForm();
        form.setUsername("");
        String message = messageDeValidation(form, "inscriptionForm",
                "username", "NotBlank", Locale.FRENCH);

        assertEquals("Le nom d'utilisateur est obligatoire.", message);
    }

    @Test
    void afficheLesBornesDeLadresseEnAnglais() {
        InscriptionForm form = new InscriptionForm();
        form.setAdresse("x");
        String message = messageDeValidation(form, "inscriptionForm",
                "adresse", "Size", Locale.ENGLISH);

        assertEquals("Address must be between 10 and 500 characters.", message);
    }

    @Test
    void afficheLesBornesDeLadresseDuProfilEnAnglais() {
        ProfileForm form = new ProfileForm();
        form.setAdresse("x");
        String message = messageDeValidation(form, "profileForm",
                "adresse", "Size", Locale.ENGLISH);

        assertEquals("Address must be between 10 and 500 characters.", message);
    }

    @Test
    void afficheLesBornesEtLapostropheDeLadresseDuProfilEnFrancais() {
        ProfileForm form = new ProfileForm();
        form.setAdresse("x");
        String message = messageDeValidation(form, "profileForm",
                "adresse", "Size", Locale.FRENCH);

        assertEquals("L'adresse doit contenir entre 10 et 500 caractères.", message);
    }

    private String messageDeValidation(Object form, String nomObjet, String champ,
                                       String code, Locale locale) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(form, nomObjet);
        validator.validate(form, errors);
        FieldError erreur = errors.getFieldErrors(champ).stream()
                .filter(fieldError -> code.equals(fieldError.getCode()))
                .findFirst()
                .orElseThrow();
        return messageSource.getMessage(erreur, locale);
    }
}
