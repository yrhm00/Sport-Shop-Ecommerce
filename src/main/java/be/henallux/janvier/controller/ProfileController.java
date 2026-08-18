package be.henallux.janvier.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import be.henallux.janvier.model.ProfileForm;
import be.henallux.janvier.model.User;
import be.henallux.janvier.service.UserService;

@Controller
@RequestMapping(value = "/profil")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        if (user == null) {
            return "redirect:/deconnexion";
        }

        model.addAttribute("profileForm", ProfileForm.depuis(user));
        return "profil";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                BindingResult bindingResult, Model model, Principal principal) {

        // Le username affiche vient toujours du compte connecte, jamais du formulaire.
        String username = principal.getName();
        profileForm.setUsername(username);

        if (bindingResult.hasErrors()) {
            return "profil";
        }

        if (userService.emailUtiliseParUnAutre(username, profileForm.getEmail())) {
            model.addAttribute("errorMessage", "inscription.error.emailExists");
            return "profil";
        }

        User misAJour = userService.mettreAJourProfil(username, profileForm);
        if (misAJour == null) {
            model.addAttribute("errorMessage", "error.profile.update");
            return "profil";
        }

        model.addAttribute("successMessage", "profile.updated");
        return "profil";
    }
}
