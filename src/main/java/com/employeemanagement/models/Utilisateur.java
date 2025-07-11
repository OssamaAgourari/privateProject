package com.employeemanagement.models;

import com.employeemanagement.utils.PasswordUtil;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a system user with authentication credentials
 */
public class Utilisateur {
    private int idUtilisateur;
    private String nomUtilisateur;
    private String motDePasse;
    private String role;
    private int idEmploye;
    private String employeNom;

    // Supported system roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_RH = "RH";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    private static final List<String> VALID_ROLES =
            Arrays.asList(ROLE_ADMIN, ROLE_RH, ROLE_MANAGER, ROLE_EMPLOYEE);

    // Constructors
    public Utilisateur() {}

    public Utilisateur(String nomUtilisateur, String motDePasse, String role) {
        setNomUtilisateur(nomUtilisateur);
        setMotDePasse(motDePasse);
        setRole(role);
    }

    // Getters and Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        if (idUtilisateur < 0) {
            throw new IllegalArgumentException("ID utilisateur invalide");
        }
        this.idUtilisateur = idUtilisateur;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        if (nomUtilisateur == null || nomUtilisateur.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }
        if (nomUtilisateur.length() < 4 || nomUtilisateur.length() > 20) {
            throw new IllegalArgumentException("Le nom d'utilisateur doit contenir entre 4 et 20 caractères");
        }
        this.nomUtilisateur = nomUtilisateur.trim().toLowerCase();
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        this.motDePasse = motDePasse.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || !VALID_ROLES.contains(role.toUpperCase())) {
            throw new IllegalArgumentException("Rôle invalide. Rôles valides: " + VALID_ROLES);
        }
        this.role = role.toUpperCase();
    }

    public int getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom;
    }

    // Security-related methods
    public boolean hasRole(String requiredRole) { // hasRole method to check if the user has the required role
        return this.role.equalsIgnoreCase(requiredRole);
    }

    public boolean isAdmin() { // isAdmin method to check if the user is an admin
        return hasRole(ROLE_ADMIN);
    }

    public boolean isUser() { // isUser method to check if the user is a user
        return hasRole("USER");
    }

    @Override
    public String toString() { // toString method to return the user's name and role
        return String.format("%s (%s)", nomUtilisateur, role);
    }

    @Override
    public boolean equals(Object o) { // equals method to check if the user is equal to another user
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return idUtilisateur == that.idUtilisateur;
    }

    @Override
    public int hashCode() { // hashCode method to return the user's id
        return Integer.hashCode(idUtilisateur);
    }
}