package com.employeemanagement.models;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents an employee's salary record with comprehensive financial details
 */
public class Salaire {
    private int idSalaire;
    private int employeId;
    private String employeNom;
    private int mois;
    private int annee;
    private double montant; // Salaire de base
    private double heuresSupplementaires;
    private double tauxHeuresSupplementaires;
    private double primes;
    private double avantages;
    private double retenues;
    private double cotisations;
    private double salaireBrut;
    private double deductions;
    private double salaireNet;
    private boolean paye = false;
    private LocalDate datePaiement;
    private String modePaiement;
    private String notes;

    // French month names
    private static final List<String> MOIS_FR = Arrays.asList(
            "", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    );

    // Constructors
    public Salaire() {}

    public Salaire(int employeId, int mois, int annee, double montant, double primes, double retenues) {
        setEmployeId(employeId);
        setMois(mois);
        setAnnee(annee);
        setMontant(montant);
        setPrimes(primes);
        setRetenues(retenues);
    }

    // New constructor including calculated fields for DB interaction
    public Salaire(int employeId, int mois, int annee, double montant, double heuresSupplementaires, double tauxHeuresSupplementaires, double primes, double avantages, double retenues, double cotisations, double salaireBrut, double deductions, double salaireNet) {
        this.employeId = employeId;
        this.mois = mois;
        this.annee = annee;
        this.montant = montant;
        this.heuresSupplementaires = heuresSupplementaires;
        this.tauxHeuresSupplementaires = tauxHeuresSupplementaires;
        this.primes = primes;
        this.avantages = avantages;
        this.retenues = retenues;
        this.cotisations = cotisations;
        this.salaireBrut = salaireBrut;
        this.deductions = deductions;
        this.salaireNet = salaireNet;
    }

    // Getters and Setters with validation
    public int getIdSalaire() {
        return idSalaire;
    }

    public void setIdSalaire(int idSalaire) {
        if (idSalaire < 0) {
            throw new IllegalArgumentException("ID Salaire invalide");
        }
        this.idSalaire = idSalaire;
    }

    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        if (employeId <= 0) {
            throw new IllegalArgumentException("ID Employé invalide");
        }
        this.employeId = employeId;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom != null ? employeNom.trim() : null;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        if (mois < 1 || mois > 12) {
            throw new IllegalArgumentException("Mois doit être entre 1 et 12");
        }
        this.mois = mois;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        int currentYear = Year.now().getValue();
        if (annee < 2000 || annee > currentYear + 1) {
            throw new IllegalArgumentException(String.format(
                    "Année doit être entre 2000 et %d", currentYear + 1));
        }
        this.annee = annee;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        if (montant < 0) {
            throw new IllegalArgumentException("Salaire de base doit être positif");
        }
        this.montant = montant;
    }

    public double getHeuresSupplementaires() {
        return heuresSupplementaires;
    }

    public void setHeuresSupplementaires(double heuresSupplementaires) {
        if (heuresSupplementaires < 0) {
            throw new IllegalArgumentException("Heures supplémentaires doivent être positives");
        }
        this.heuresSupplementaires = heuresSupplementaires;
    }

    public double getTauxHeuresSupplementaires() {
        return tauxHeuresSupplementaires;
    }

    public void setTauxHeuresSupplementaires(double tauxHeuresSupplementaires) {
        if (tauxHeuresSupplementaires <= 0) {
            throw new IllegalArgumentException("Taux heures supplémentaires doit être positif");
        }
        this.tauxHeuresSupplementaires = tauxHeuresSupplementaires;
    }

    public double getPrimes() {
        return primes;
    }

    public void setPrimes(double primes) {
        if (primes < 0) {
            throw new IllegalArgumentException("Primes doivent être positives");
        }
        this.primes = primes;
    }

    public double getAvantages() {
        return avantages;
    }

    public void setAvantages(double avantages) {
        if (avantages < 0) {
            throw new IllegalArgumentException("Avantages doivent être positifs");
        }
        this.avantages = avantages;
    }

    public double getRetenues() {
        return retenues;
    }

    public void setRetenues(double retenues) {
        if (retenues < 0) {
            throw new IllegalArgumentException("Retenues doivent être positives");
        }
        this.retenues = retenues;
    }

    public double getCotisations() {
        return cotisations;
    }

    public void setCotisations(double cotisations) {
        if (cotisations < 0) {
            throw new IllegalArgumentException("Cotisations doivent être positives");
        }
        this.cotisations = cotisations;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Getters and setters for DB mapped fields

    public void setSalaireBrut(double salaireBrut) {
        if (salaireBrut < 0) {
            throw new IllegalArgumentException("Salaire brut doit être positif");
        }
        this.salaireBrut = salaireBrut;
    }

    public double getDeductions() {
        return deductions;
    }

    public void setDeductions(double deductions) {
        if (deductions < 0) {
            throw new IllegalArgumentException("Deductions doivent être positives");
        }
        this.deductions = deductions;
    }

    // Renamed getter for the stored net salary
    public double getStoredSalaireNet() {
        return salaireNet;
    }

    public void setSalaireNet(double salaireNet) {
        if (salaireNet < 0) {
            throw new IllegalArgumentException("Salaire net doit être positif");
        }
        this.salaireNet = salaireNet;
    }

    // Business logic methods
    public double getSalaireBrut() {
        return montant + primes + avantages + (heuresSupplementaires * tauxHeuresSupplementaires);
    }

    public double getSalaireNet() {
        return getSalaireBrut() - retenues - cotisations;
    }

    public String getMoisName() {
        return MOIS_FR.get(mois);
    }

    public Month getMonth() {
        return Month.of(mois);
    }

    public boolean isForCurrentMonth() {
        LocalDate now = LocalDate.now();
        return mois == now.getMonthValue() && annee == now.getYear();
    }

    public boolean isForPeriod(int mois, int annee) {
        return this.mois == mois && this.annee == annee;
    }

    public String getPeriode() {
        return String.format("%s %d", getMoisName(), annee);
    }

    public void marquerCommePaye(String modePaiement) {
        this.paye = true;
        this.datePaiement = LocalDate.now();
        this.modePaiement = modePaiement;
    }

    public String getFormattedSalaireBrut() {
        return String.format("%,.2f €", getSalaireBrut());
    }

    public String getFormattedSalaireNet() {
        return String.format("%,.2f €", getSalaireNet());
    }

    @Override
    public String toString() {
        return String.format("Salaire [%s] - Brut: %s - Net: %s",
                getPeriode(), getFormattedSalaireBrut(), getFormattedSalaireNet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salaire salaire = (Salaire) o;
        return idSalaire == salaire.idSalaire;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSalaire);
    }

    // Builder pattern
    public static class Builder { // Builder class to build the Salaire
        private final Salaire salaire = new Salaire(); // create a new Salaire

        public Builder forEmployee(int employeId, String employeNom) { // forEmployee method to set the employeId and employeNom
            salaire.employeId = employeId;
            salaire.employeNom = employeNom;
            return this;
        }

        public Builder forPeriod(int mois, int annee) { // forPeriod method to set the mois and annee
            salaire.mois = mois;
            salaire.annee = annee;
            return this;
        }

        public Builder withBaseSalary(double montant) { // withBaseSalary method to set the montant
            salaire.montant = montant;
            return this;
        }

        public Builder withOvertime(double heures, double taux) { // withOvertime method to set the heures and taux
            salaire.heuresSupplementaires = heures;
            salaire.tauxHeuresSupplementaires = taux;
            return this;
        }

        public Builder withBonuses(double primes, double avantages) { // withBonuses method to set the primes and avantages
            salaire.primes = primes;
            salaire.avantages = avantages;
            return this;
        }

        public Builder withDeductions(double retenues, double cotisations) { // withDeductions method to set the retenues and cotisations
            salaire.retenues = retenues;
            salaire.cotisations = cotisations;
            return this;
        }

        public Builder withPaymentInfo(String modePaiement, String notes) { // withPaymentInfo method to set the modePaiement and notes
            salaire.modePaiement = modePaiement;
            salaire.notes = notes;
            return this;
        }

        public Salaire build() { // build method to build the Salaire
            // Validate required fields
            if (salaire.employeId <= 0) {
                throw new IllegalStateException("ID Employé est requis");
            }
            if (salaire.mois < 1 || salaire.mois > 12) {
                throw new IllegalStateException("Mois invalide");
            }
            if (salaire.montant <= 0) {
                throw new IllegalStateException("Salaire de base doit être positif");
            }
            return salaire;
        }
    }
}