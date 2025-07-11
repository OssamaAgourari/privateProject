package com.employeemanagement.views.dialogs;

import com.employeemanagement.dao.EmployeDAO;
import com.employeemanagement.dao.SalaireDAO;
import com.employeemanagement.models.Employe;
import com.employeemanagement.models.Salaire;
import com.employeemanagement.utils.DateUtils;
import com.employeemanagement.utils.UIStyleManager;
import com.employeemanagement.utils.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class SalaireDialog extends JDialog {
    private boolean confirmed = false;
    private Optional<Salaire> salaire;

    // Form components
    private JComboBox<Employe> employeCombo;
    private JSpinner moisSpinner, anneeSpinner;
    private JTextField montantField, primesField, retenuesField;

    // DAOs
    private final EmployeDAO employeDAO;
    private final SalaireDAO salaireDAO;

    public SalaireDialog(Frame parent, Optional<Salaire> salaire) {
        super(parent, salaire.isPresent() ? "Modifier Salaire" : "Ajouter Salaire", true);
        this.salaire = salaire;
        this.employeDAO = new EmployeDAO();
        this.salaireDAO = new SalaireDAO();
        initializeComponents();

        if (salaire.isPresent()) {
            populateFields(salaire.get());
        }

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        UIStyleManager.stylePanel((JPanel) this.getContentPane());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // Increased rows and gaps
        UIStyleManager.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Increased padding

        JLabel employeLabel = new JLabel("Employé:");
        UIStyleManager.styleLabel(employeLabel);
        formPanel.add(employeLabel);
        employeCombo = new JComboBox<>();
        UIStyleManager.styleComboBox(employeCombo);
        loadEmployes(); // Call method to load employees
        formPanel.add(employeCombo);

        JLabel moisLabel = new JLabel("Mois:");
        UIStyleManager.styleLabel(moisLabel);
        formPanel.add(moisLabel);
        SpinnerNumberModel moisModel = new SpinnerNumberModel(1, 1, 12, 1);
        moisSpinner = new JSpinner(moisModel);
        // UIStyleManager.styleSpinner(moisSpinner); // No specific spinner style yet, UIManager default applies
        formPanel.add(moisSpinner);

        JLabel anneeLabel = new JLabel("Année:");
        UIStyleManager.styleLabel(anneeLabel);
        formPanel.add(anneeLabel);
        SpinnerNumberModel anneeModel = new SpinnerNumberModel(2024, 2000, 2100, 1);
        anneeSpinner = new JSpinner(anneeModel);
        // UIStyleManager.styleSpinner(anneeSpinner);
        formPanel.add(anneeSpinner);

        JLabel montantLabel = new JLabel("Montant:");
        UIStyleManager.styleLabel(montantLabel);
        formPanel.add(montantLabel);
        montantField = new JTextField(10);
        UIStyleManager.styleTextField(montantField);
        formPanel.add(montantField);

        JLabel primesLabel = new JLabel("Primes:");
        UIStyleManager.styleLabel(primesLabel);
        formPanel.add(primesLabel);
        primesField = new JTextField(10);
        UIStyleManager.styleTextField(primesField);
        formPanel.add(primesField);

        JLabel retenuesLabel = new JLabel("Retenues:");
        UIStyleManager.styleLabel(retenuesLabel);
        formPanel.add(retenuesLabel);
        retenuesField = new JTextField(10);
        UIStyleManager.styleTextField(retenuesField);
        formPanel.add(retenuesField);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); // Increased gaps
        UIStyleManager.stylePanel(buttonPanel);

        JButton saveButton = new JButton("Enregistrer");
        UIStyleManager.styleButton(saveButton);
        saveButton.addActionListener(e -> save());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Annuler");
        UIStyleManager.styleButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        // Add components to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEmployes() {
        try {
            List<Employe> employes = employeDAO.getAllActiveEmployes(); // Assuming this method exists
            employeCombo.removeAllItems();
            for (Employe emp : employes) {
                employeCombo.addItem(emp);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des employés: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields(Salaire salaire) {
        // Populate fields from the Salaire object
        // Assuming Employe is correctly represented in the ComboBox
        for (int i = 0; i < employeCombo.getItemCount(); i++) {
            if (employeCombo.getItemAt(i).getIdEmploye() == salaire.getEmployeId()) {
                employeCombo.setSelectedIndex(i);
                break;
            }
        }
        moisSpinner.setValue(salaire.getMois());
        anneeSpinner.setValue(salaire.getAnnee());
        montantField.setText(String.valueOf(salaire.getMontant()));
        primesField.setText(String.valueOf(salaire.getPrimes()));
        retenuesField.setText(String.valueOf(salaire.getRetenues()));
    }

    private boolean validateInput() {
        // Basic validation for required numeric fields
        if (montantField.getText().trim().isEmpty() || primesField.getText().trim().isEmpty() || retenuesField.getText().trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs numériques.", "Validation", JOptionPane.WARNING_MESSAGE);
             return false;
        }
        
        if (!ValidationUtil.isValidDouble(montantField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant valide.", "Validation", JOptionPane.WARNING_MESSAGE);
            montantField.requestFocusInWindow();
            return false;
        }
         if (!ValidationUtil.isValidDouble(primesField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant de primes valide.", "Validation", JOptionPane.WARNING_MESSAGE);
            primesField.requestFocusInWindow();
            return false;
        }
         if (!ValidationUtil.isValidDouble(retenuesField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant de retenues valide.", "Validation", JOptionPane.WARNING_MESSAGE);
            retenuesField.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private Salaire getSalaire() {
        Salaire currentSalaire = salaire.orElseGet(Salaire::new);
        
        Employe selectedEmploye = (Employe) employeCombo.getSelectedItem();
        if (selectedEmploye != null) {
            currentSalaire.setEmployeId(selectedEmploye.getIdEmploye());
            currentSalaire.setEmployeNom(selectedEmploye.getNom() + " " + selectedEmploye.getPrenom()); // Assuming Employe model has getNom and getPrenom
        }
        
        currentSalaire.setMois((Integer) moisSpinner.getValue());
        currentSalaire.setAnnee((Integer) anneeSpinner.getValue());
        currentSalaire.setMontant(Double.parseDouble(montantField.getText().trim()));
        currentSalaire.setPrimes(Double.parseDouble(primesField.getText().trim()));
        currentSalaire.setRetenues(Double.parseDouble(retenuesField.getText().trim()));
        
        // The Salaire class calculates net salary internally, no need to set it here.

        return currentSalaire;
    }

    private void save() {
        if (validateInput()) {
            // Add check for selected employee
            if (employeCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un employé pour le salaire.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Get Salaire object from form fields with raw input
                Salaire salaireToSave = getSalaire();

                // Calculate and set derived fields (salaireBrut, deductions, salaireNet)
                double montant = salaireToSave.getMontant();
                double primes = salaireToSave.getPrimes();
                double retenues = salaireToSave.getRetenues();
                // Assuming for now that heuresSupplementaires, tauxHeuresSupplementaires, avantages, and cotisations are 0 if not in UI
                double heuresSupplementaires = 0;
                double tauxHeuresSupplementaires = 0;
                double avantages = 0;
                double cotisations = 0;

                double salaireBrut = montant + primes + avantages + (heuresSupplementaires * tauxHeuresSupplementaires);
                double deductions = retenues + cotisations;
                double salaireNet = salaireBrut - deductions;

                salaireToSave.setSalaireBrut(salaireBrut);
                salaireToSave.setDeductions(deductions);
                salaireToSave.setSalaireNet(salaireNet);

                // Set the payment date to the current date before saving
                salaireToSave.setDatePaiement(java.time.LocalDate.now());

                // If it's an existing salary, set its ID
                if (this.salaire.isPresent() && this.salaire.get().getIdSalaire() != 0) {
                    salaireToSave.setIdSalaire(this.salaire.get().getIdSalaire());
                }

                // Save to database
                salaireDAO.save(salaireToSave);
                System.out.println("Salaire saved successfully.");
                confirmed = true;
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'enregistrement du salaire: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}