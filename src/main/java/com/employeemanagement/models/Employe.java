package com.employeemanagement.models;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Represents an employee in the system with comprehensive personal and professional information
 */
public class Employe {
    private int idEmploye;
    private String nom;
    private String prenom;
    private String poste;
    private int serviceId;
    private String serviceName;
    private LocalDate dateEmbauche;
    private LocalDate dateNaissance;
    private double salaireDeBase;
    private String cin;
    private String cnss;
    private String telephone;
    private String email;
    private String adresse;
    private String statut; // CDI, CDD, etc.
    private String typeContrat;
    private String photoPath;
    private boolean actif = true;

    // Constructors
    public Employe() {}

    public Employe(String nom, String prenom, String poste, int serviceId,
                   LocalDate dateEmbauche, double salaireDeBase) { // constructor with parameters
        this.nom = validateNom(nom);
        this.prenom = validatePrenom(prenom);
        this.poste = validatePoste(poste);
        this.serviceId = serviceId;
        this.dateEmbauche = validateDateEmbauche(dateEmbauche);
        this.salaireDeBase = validateSalaire(salaireDeBase);
    }

    // Validation methods
    private String validateNom(String nom) { // validate the nom
        if (nom == null || nom.trim().isEmpty()) { // if the nom is null or empty
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (!nom.matches("[a-zA-ZÀ-ÿ-' ]+")) { // if the nom contains invalid characters
            throw new IllegalArgumentException("Le nom contient des caractères invalides");
        }
        return nom.trim();
    }

    private String validatePrenom(String prenom) { // validate the prenom
        if (prenom == null || prenom.trim().isEmpty()) { // if the prenom is null or empty
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        if (!prenom.matches("[a-zA-ZÀ-ÿ-' ]+")) { // if the prenom contains invalid characters
            throw new IllegalArgumentException("Le prénom contient des caractères invalides");
        }
        return prenom.trim();
    }

    private String validatePoste(String poste) { // validate the poste
        if (poste == null || poste.trim().isEmpty()) { // if the poste is null or empty
            throw new IllegalArgumentException("Le poste ne peut pas être vide");
        }
        return poste.trim();
    }

    private LocalDate validateDateEmbauche(LocalDate date) { // validate the dateEmbauche
        if (date == null) { // if the date is null
            throw new IllegalArgumentException("La date d'embauche ne peut pas être nulle");
        }
        if (date.isAfter(LocalDate.now())) { // if the date is in the future
            throw new IllegalArgumentException("La date d'embauche ne peut pas être dans le futur");
        }
        if (dateNaissance != null && Period.between(dateNaissance, date).getYears() < 16) { // if the dateNaissance is not null and the age is less than 16
            throw new IllegalArgumentException("L'employé doit avoir au moins 16 ans lors de l'embauche");
        }
        return date;
    }

    private double validateSalaire(double salaire) { // validate the salaire
        if (salaire < 0) { // if the salaire is less than 0
            throw new IllegalArgumentException("Le salaire doit être positif");
        }
        return salaire;
    }

    private String validateEmail(String email) { // validate the email
        if (email != null && !email.isEmpty()) { // if the email is not null and not empty
            if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // if the email is not valid
                throw new IllegalArgumentException("Format d'email invalide");
            }
        }
        return email;
    }

    // Getters and Setters with validation
    public int getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = validateNom(nom);
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = validatePrenom(prenom);
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = validatePoste(poste);
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = validateDateEmbauche(dateEmbauche);
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
        // Revalidate hire date if birth date is set after
        if (this.dateEmbauche != null) {
            validateDateEmbauche(this.dateEmbauche);
        }
    }

    public double getSalaireDeBase() {
        return salaireDeBase;
    }

    public void setSalaireDeBase(double salaireDeBase) {
        this.salaireDeBase = validateSalaire(salaireDeBase);
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getCnss() {
        return cnss;
    }

    public void setCnss(String cnss) {
        this.cnss = cnss;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = validateEmail(email);
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    // Helper methods
    public String getFullName() {
        return prenom + " " + nom;
    }

    public int getAnciennete() { 
        return Period.between(dateEmbauche, LocalDate.now()).getYears();
    }

    public int getAge() {
        return dateNaissance != null ? Period.between(dateNaissance, LocalDate.now()).getYears() : -1;
    }

    public String getFormattedSalaire() {
        return String.format("%,.2f €", salaireDeBase); // %,.2f € is the format for the salaire
    }

    @Override
    public String toString() { // toString method to return the employee's name and poste
        return String.format("%s %s (%s)", prenom, nom, poste);
    }

    // Equality check based on ID
    @Override
    public boolean equals(Object o) { // equals method to check if two objects are equal
        if (this == o) return true; // if the objects are the same object 
        if (o == null || getClass() != o.getClass()) return false; // if the object is null or the class is not the same
        Employe employe = (Employe) o; // cast the object to an Employe
        return idEmploye == employe.idEmploye; // check if the idEmploye is the same
    }

    @Override
    public int hashCode() { // hashCode method to return the employee's id
        return Objects.hash(idEmploye);
    }

    // Builder pattern for easier object creation
    public static class Builder {
        private final Employe employe = new Employe(); // create a new Employe

        public Builder withId(int id) { // withId method to set the idEmploye
            employe.idEmploye = id;
            return this;
        }

        public Builder withName(String nom, String prenom) { // withName method to set the nom and prenom
            employe.nom = nom;
            employe.prenom = prenom;
            return this;
        }

        public Builder withPoste(String poste) { // withPoste method to set the poste
            employe.poste = poste;
            return this;
        }

        public Builder withService(int serviceId, String serviceName) { // withService method to set the serviceId and serviceName
            employe.serviceId = serviceId;
            employe.serviceName = serviceName;
            return this;
        }

        public Builder withDates(LocalDate dateNaissance, LocalDate dateEmbauche) { // withDates method to set the dateNaissance and dateEmbauche
            employe.dateNaissance = dateNaissance;
            employe.dateEmbauche = dateEmbauche;
            return this;
        }

        public Builder withContactInfo(String telephone, String email, String adresse) { // withContactInfo method to set the telephone, email and adresse
            employe.telephone = telephone;
            employe.email = email;
            employe.adresse = adresse;
            return this;
        }

        public Builder withAdministrative(String cin, String cnss) { // withAdministrative method to set the cin and cnss
            employe.cin = cin;
            employe.cnss = cnss;
            return this;
        }

        public Builder withContractInfo(String statut, String typeContrat, double salaireDeBase) { // withContractInfo method to set the statut, typeContrat and salaireDeBase
            employe.statut = statut;
            employe.typeContrat = typeContrat;
            employe.salaireDeBase = salaireDeBase;
            return this;
        }

        public Employe build() { // build method to build the Employe
            // Validate required fields
            if (employe.nom == null || employe.prenom == null || employe.poste == null) {
                throw new IllegalStateException("Nom, prénom et poste sont obligatoires");
            }
            if (employe.dateEmbauche == null) {
                throw new IllegalStateException("La date d'embauche est obligatoire");
            }
            return employe;
        }
    }
}