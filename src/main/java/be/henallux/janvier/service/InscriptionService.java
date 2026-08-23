package be.henallux.janvier.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import be.henallux.janvier.dataAccess.dao.UserDataAccess;
import be.henallux.janvier.model.Authority;
import be.henallux.janvier.model.InscriptionForm;
import be.henallux.janvier.model.User;

@Service
public class InscriptionService {

    private final UserDataAccess userDAO;
    private final PasswordEncoder passwordEncoder;
    private final SanitizationService sanitizationService;

    @Autowired
    public InscriptionService(UserDataAccess userDAO, PasswordEncoder passwordEncoder,
                              SanitizationService sanitizationService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.sanitizationService = sanitizationService;
    }

    /**
     * Valide que les deux mots de passe sont identiques
     */
    public boolean passwordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Vérifie si le username existe déjà
     */
    public boolean usernameExists(String username) {
        return userDAO.existsByUsername(username);
    }

    /**
     * Vérifie si l'email existe déjà
     */
    public boolean emailExists(String email) {
        return userDAO.existsByEmail(email);
    }

    /**
     * Crée un nouvel utilisateur à partir du formulaire d'inscription
     */
    public User createUser(InscriptionForm form) {
        // Créer l'utilisateur
        // Les champs libres sont nettoyes de tout balisage HTML avant enregistrement.
        User user = new User();
        // Le username a deja ete controle et refuse en cas de modification par
        // le sanitizer dans InscriptionController, comme demande au laboratoire.
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setNom(sanitizationService.nettoyer(form.getNom()));
        user.setPrenom(sanitizationService.nettoyer(form.getPrenom()));
        user.setEmail(sanitizationService.nettoyer(form.getEmail()));
        user.setTelephone(sanitizationService.nettoyer(form.getTelephone()));
        user.setAdresse(sanitizationService.nettoyer(form.getAdresse()));
        user.setCodePostal(sanitizationService.nettoyer(form.getCodePostal()));
        user.setLocalite(sanitizationService.nettoyer(form.getLocalite()));
        user.setEnabled(true);

        
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority("ROLE_USER"));
        user.setAuthorities(authorities);

        // Sauvegarder en base de données
        return userDAO.save(user);
    }
}

