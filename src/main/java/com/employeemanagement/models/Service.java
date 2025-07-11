package com.employeemanagement.models;

/**
 * Represents a department/service in the organization
 */
public class Service {
    private int idService;
    private String nom;
    private String description;

    // Constructors
    public Service() {}

    public Service(String nom, String description) { // constructor with parameters
        setNom(nom);
        setDescription(description);
    }

    public Service(int idService, String nom, String description) { // constructor with parameters
        this(nom, description); // Reuse the other constructor
        setIdService(idService);
    }

    // Getters and Setters with validation
    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        if (idService < 0) {
            throw new IllegalArgumentException("ID Service invalide");
        }
        this.idService = idService;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du service ne peut pas être vide");
        }
        this.nom = nom.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    // Business methods
    public boolean isValid() {
        return nom != null && !nom.isEmpty();
    }

    public String getInfo() {
        return String.format("%s%s",
                nom,
                description != null ? " - " + description : "");
    }

    // Equality and display
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        Service service = (Service) o;
        return idService == service.idService;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idService);
    }

    @Override
    public String toString() {
        return getNom(); // For ComboBox display
    }
}