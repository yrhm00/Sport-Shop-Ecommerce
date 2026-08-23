package be.henallux.janvier.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Formulaire de modification du compte utilisateur.
 *
 * Classe distincte du modele User (qui sert de principal a Spring Security) :
 * on ne valide et ne lie que les champs que l'utilisateur a le droit de modifier.
 * Le username n'est jamais modifiable.
 */
public class ProfileForm {

    private String username;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank
    @Size(min = 2, max = 100)
    private String prenom;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Pattern(regexp = "^$|^[0-9+ ]{9,20}$")
    private String telephone;

    @NotBlank
    @Size(min = 10, max = 500)
    private String adresse;

    @NotBlank
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[0-9]*$")
    private String codePostal;

    @NotBlank
    @Size(min = 2, max = 100)
    private String localite;

    public ProfileForm() {
    }

    /** Pre-remplit le formulaire a partir de l'utilisateur connecte. */
    public static ProfileForm depuis(User user) {
        ProfileForm form = new ProfileForm();
        if (user != null) {
            form.setUsername(user.getUsername());
            form.setNom(user.getNom());
            form.setPrenom(user.getPrenom());
            form.setEmail(user.getEmail());
            form.setTelephone(user.getTelephone());
            form.setAdresse(user.getAdresse());
            form.setCodePostal(user.getCodePostal());
            form.setLocalite(user.getLocalite());
        }
        return form;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }
}
