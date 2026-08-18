package be.henallux.janvier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.dao.UserDataAccess;
import be.henallux.janvier.model.ProfileForm;
import be.henallux.janvier.model.User;

/**
 * Service metier du compte utilisateur.
 * Le controleur ne touche jamais directement a la couche d'acces aux donnees.
 */
@Service
public class UserService {

    private final UserDataAccess userDAO;
    private final SanitizationService sanitizationService;

    @Autowired
    public UserService(UserDataAccess userDAO, SanitizationService sanitizationService) {
        this.userDAO = userDAO;
        this.sanitizationService = sanitizationService;
    }

    public User getByUsername(String username) {
        if (username == null) {
            return null;
        }
        return userDAO.findByUsername(username);
    }

    /**
     * Met a jour les donnees modifiables du compte.
     *
     * Le username et le mot de passe ne sont jamais pris depuis le formulaire :
     * l'utilisateur courant est relu en base et seuls les champs autorises sont
     * ecrases, apres nettoyage anti-XSS.
     *
     * @return l'utilisateur mis a jour, ou null s'il n'existe pas.
     */
    @Transactional
    public User mettreAJourProfil(String username, ProfileForm form) {
        User utilisateur = getByUsername(username);
        if (utilisateur == null || form == null) {
            return null;
        }

        utilisateur.setNom(sanitizationService.nettoyer(form.getNom()));
        utilisateur.setPrenom(sanitizationService.nettoyer(form.getPrenom()));
        utilisateur.setEmail(sanitizationService.nettoyer(form.getEmail()));
        utilisateur.setTelephone(sanitizationService.nettoyer(form.getTelephone()));
        utilisateur.setAdresse(sanitizationService.nettoyer(form.getAdresse()));
        utilisateur.setCodePostal(sanitizationService.nettoyer(form.getCodePostal()));
        utilisateur.setLocalite(sanitizationService.nettoyer(form.getLocalite()));

        return userDAO.save(utilisateur);
    }

    /** Vrai si l'adresse email est deja utilisee par un AUTRE compte. */
    public boolean emailUtiliseParUnAutre(String username, String email) {
        if (email == null) {
            return false;
        }
        User proprietaire = userDAO.findByEmail(email);
        return proprietaire != null && !proprietaire.getUsername().equals(username);
    }
}
