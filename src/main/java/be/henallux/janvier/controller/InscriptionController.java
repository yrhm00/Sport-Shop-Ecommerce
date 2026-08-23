package be.henallux.janvier.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import be.henallux.janvier.model.InscriptionForm;
import be.henallux.janvier.service.InscriptionService;
import be.henallux.janvier.service.SanitizationService;

@Controller
@RequestMapping(value="/inscription")
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final SanitizationService sanitizationService;

    @Autowired
    public InscriptionController(InscriptionService inscriptionService,
                                 SanitizationService sanitizationService) {
        this.inscriptionService = inscriptionService;
        this.sanitizationService = sanitizationService;
    }

    @GetMapping
    public String showInscription(Model model) {
        model.addAttribute("inscriptionForm", new InscriptionForm());
        return "inscription";
    }

    @PostMapping
    public String processInscription(@Valid @ModelAttribute("inscriptionForm") InscriptionForm form,
                                      BindingResult bindingResult,
                                      Model model) {
        
        // Vérification des erreurs de validation
        if (bindingResult.hasErrors()) {
            return "inscription";
        }

        // Labo Spring Security 7 : le username est nettoye dans le controleur
        // et l'inscription est refusee si le sanitizer modifie la saisie.
        String usernameNettoye = sanitizationService.nettoyer(form.getUsername());
        if (!Objects.equals(form.getUsername(), usernameNettoye)) {
            model.addAttribute("errorMessage", "inscription.error.usernameUnsafe");
            return "inscription";
        }

        // Vérification que les mots de passe correspondent
        if (!inscriptionService.passwordsMatch(form.getPassword(), form.getConfirmPassword())) {
            model.addAttribute("errorMessage", "inscription.error.passwordMismatch");
            return "inscription";
        }

        // Vérification que le username n'existe pas déjà
        if (inscriptionService.usernameExists(form.getUsername())) {
            model.addAttribute("errorMessage", "inscription.error.usernameExists");
            return "inscription";
        }

        // Vérification que l'email n'existe pas déjà
        if (inscriptionService.emailExists(form.getEmail())) {
            model.addAttribute("errorMessage", "inscription.error.emailExists");
            return "inscription";
        }

        // Créer l'utilisateur
        inscriptionService.createUser(form);

        return "redirect:/connexion?inscriptionSuccess";
    }
}
