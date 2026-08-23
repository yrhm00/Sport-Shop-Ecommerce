package be.henallux.janvier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

import be.henallux.janvier.model.InscriptionForm;
import be.henallux.janvier.service.InscriptionService;
import be.henallux.janvier.service.SanitizationService;

class InscriptionControllerTest {

    private InscriptionController controller;

    @Mock
    private InscriptionService inscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new InscriptionController(inscriptionService, new SanitizationService());
    }

    @Test
    void usernameModifieParLeSanitizerRefuseLInscription() {
        InscriptionForm form = formulaireValide();
        form.setUsername("<b>utilisateur</b>");
        ExtendedModelMap model = new ExtendedModelMap();

        String vue = controller.processInscription(form,
                new BeanPropertyBindingResult(form, "inscriptionForm"), model);

        assertEquals("inscription", vue);
        assertEquals("inscription.error.usernameUnsafe", model.get("errorMessage"));
        verifyNoInteractions(inscriptionService);
    }

    private InscriptionForm formulaireValide() {
        InscriptionForm form = new InscriptionForm();
        form.setUsername("utilisateur");
        form.setPassword("MotDePasse2026!");
        form.setConfirmPassword("MotDePasse2026!");
        form.setNom("Dupont");
        form.setPrenom("Jean");
        form.setEmail("jean.dupont@email.be");
        form.setTelephone("0470000000");
        form.setAdresse("10 Avenue des Tilleuls");
        form.setCodePostal("4000");
        form.setLocalite("Liege");
        return form;
    }
}
