package com.employeemanagement.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

/**
 * Represents an employee's time tracking record (pointage)
 */
public class Pointage {
    private int id;
    private int idPointage;
    private int employeId;
    private String employeNom;
    private LocalDate date;
    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    private String notes;
    private Duration duree;

    // Constructors
    public Pointage() {}

    public Pointage(int employeId, LocalDate date, LocalTime heureArrivee, LocalTime heureDepart) {
        setEmployeId(employeId);
        setDate(date);
        setHeureArrivee(heureArrivee);
        setHeureDepart(heureDepart);
    }

    // Getters and Setters with validation
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPointage() {
        return idPointage;
    }

    public void setIdPointage(int idPointage) {
        if (idPointage < 0) {
            throw new IllegalArgumentException("ID de pointage invalide");
        }
        this.idPointage = idPointage;
    }

    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        if (employeId <= 0) {
            throw new IllegalArgumentException("ID employé invalide");
        }
        this.employeId = employeId;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom != null ? employeNom.trim() : null;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date de pointage invalide");
        }
        this.date = date;
    }

    public LocalTime getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(LocalTime heureArrivee) {
        validateHeure(heureArrivee, "Arrivée");
        this.heureArrivee = heureArrivee;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        validateHeure(heureDepart, "Départ");
        if (heureArrivee != null && heureDepart != null && heureDepart.isBefore(heureArrivee)) {
            throw new IllegalArgumentException("L'heure de départ doit être après l'heure d'arrivée");
        }
        this.heureDepart = heureDepart;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Duration getDuree() {
        return duree;
    }

    // Business logic methods
    public double getHeuresTravaillees() {
        if (heureArrivee != null && heureDepart != null) {
            return Duration.between(heureArrivee, heureDepart).toMinutes() / 60.0;
        }
        return 0;
    }

    public boolean isComplete() {
        return heureArrivee != null && heureDepart != null;
    }

    public String getDureeFormatee() {
        if (!isComplete()) return "N/A";

        Duration duration = Duration.between(heureArrivee, heureDepart);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%dh%02d", hours, minutes);
    }

    /**
     * Calculates work duration between arrival and departure times
     */
    public void calculateDuree() {
        if (heureArrivee != null && heureDepart != null) {
            this.duree = Duration.between(heureArrivee, heureDepart);
        } else {
            this.duree = null;
        }
    }

    // Helper methods
    private void validateHeure(LocalTime heure, String type) {
        if (heure != null) {
            if (heure.isBefore(LocalTime.of(4, 0))) {
                throw new IllegalArgumentException(
                        String.format("L'heure %s ne peut pas être avant 4h du matin", type.toLowerCase())
                );
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Pointage [%s] %s - Arrivée: %s, Départ: %s",
                date,
                employeNom != null ? employeNom : "Employé#" + employeId,
                heureArrivee != null ? heureArrivee : "N/A",
                heureDepart != null ? heureDepart : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pointage)) return false;
        Pointage pointage = (Pointage) o;
        return idPointage == pointage.idPointage;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idPointage);
    }
}