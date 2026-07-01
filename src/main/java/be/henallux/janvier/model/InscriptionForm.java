package be.henallux.janvier.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Classe modèle pour le formulaire d'inscription
 */
public class InscriptionForm {

    @NotNull
    @Size(min = 2, max = 50)
    private String username;

    @NotNull
    @Size(min = 15, max = 64)
    private String password;

    @NotNull
    @Size(min = 15, max = 64)
    private String confirmPassword;

    @NotNull
    @Size(min = 2, max = 100)
    private String nom;

    @NotNull
    @Size(min = 2, max = 100)
    private String prenom;

    @NotNull
    @Email
    @Size(max = 150)
    private String email;

    @Pattern(regexp = "^$|^[0-9+ ]{9,20}$", message = "{Pattern.inscriptionForm.telephone}")
    private String telephone;

    @NotNull
    @Size(min = 10, max = 500)
    private String adresse;

    @NotNull
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[0-9]*$", message = "Le code postal ne doit contenir que des chiffres.")
    private String codePostal;

    @NotNull
    @Size(min = 2, max = 100)
    private String localite;

    // ... existing getters/setters ...

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

    // Constructeur
    public InscriptionForm() {
    }

    // Getters et Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
}


