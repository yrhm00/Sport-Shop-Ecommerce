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

    @NotBlank(message = "{NotNull.inscriptionForm.nom}")
    @Size(min = 2, max = 100, message = "{Size.inscriptionForm.nom}")
    private String nom;

    @NotBlank(message = "{NotNull.inscriptionForm.prenom}")
    @Size(min = 2, max = 100, message = "{Size.inscriptionForm.prenom}")
    private String prenom;

    @NotBlank(message = "{NotNull.inscriptionForm.email}")
    @Email(message = "{Email.inscriptionForm.email}")
    @Size(max = 150, message = "{Size.inscriptionForm.email}")
    private String email;

    @Pattern(regexp = "^$|^[0-9+ ]{9,20}$", message = "{Pattern.inscriptionForm.telephone}")
    private String telephone;

    @NotBlank(message = "{NotNull.inscriptionForm.adresse}")
    @Size(min = 10, max = 500, message = "{Size.inscriptionForm.adresse}")
    private String adresse;

    @NotBlank(message = "{NotNull.inscriptionForm.codePostal}")
    @Size(min = 4, max = 10, message = "{Size.inscriptionForm.codePostal}")
    @Pattern(regexp = "^[0-9]*$", message = "{Pattern.inscriptionForm.codePostal}")
    private String codePostal;

    @NotBlank(message = "{NotNull.inscriptionForm.localite}")
    @Size(min = 2, max = 100, message = "{Size.inscriptionForm.localite}")
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
