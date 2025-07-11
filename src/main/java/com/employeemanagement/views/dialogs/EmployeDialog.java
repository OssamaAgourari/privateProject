package com.employeemanagement.views.dialogs;

import com.employeemanagement.dao.EmployeDAO;
import com.employeemanagement.dao.ServiceDAO;
import com.employeemanagement.models.Employe;
import com.employeemanagement.models.Service;
import com.employeemanagement.utils.PdfExporter;
import com.employeemanagement.utils.ValidationUtil;
import com.employeemanagement.utils.UIStyleManager;

import javax.swing.*; // Import necessary Swing components: JDialog, JPanel, JTextField, JComboBox, JSpinner, JButton, JLabel, JOptionPane, JFileChooser;
import java.awt.*;
import java.awt.event.ActionEvent;                      // Swing est une bibliothèque graphique de Java qui te permet de créer
import java.io.File;                                    //des interfaces utilisateur avec des fenêtres, boutons, tableaux, menus, etc.
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeDialog extends JDialog {
    private final Employe employe;
    private boolean confirmed = false;

    // Form components
    private JTextField nomField, prenomField, posteField, salaireField; // JTextField for text input
    private JComboBox<Service> serviceCombo; // JComboBox for selecting from a list of services
    private JSpinner dateSpinner; // JSpinner for selecting dates
    private JButton exportButton; // JButton for exporting employee details

    // New Form components
    private JTextField cinField, cnssField, telephoneField, emailField, adresseField, statutField, typeContratField;

    // DAOs
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final EmployeDAO employeDAO = new EmployeDAO();

    public EmployeDialog(JFrame parent, Optional<Employe> employeOpt) { // JFrame is the main window of the application
        super(parent, employeOpt.isPresent() ? "Modifier Employé" : "Ajouter Employé", true);
        this.employe = employeOpt.orElse(new Employe());
        initializeUI(); 
        populateFields();
        setSize(500, 600);
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        UIStyleManager.stylePanel(mainPanel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        UIStyleManager.stylePanel(formPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;

        // Nom field
        addFormLabel("Nom*:", gbc, formPanel, 0, row);
        nomField = addFormTextField(gbc, formPanel, 1, row++, 25);

        // Prénom field
        addFormLabel("Prénom*:", gbc, formPanel, 0, row);
        prenomField = addFormTextField(gbc, formPanel, 1, row++, 25);

        // Poste field
        addFormLabel("Poste*:", gbc, formPanel, 0, row);
        posteField = addFormTextField(gbc, formPanel, 1, row++, 25);

        // Service combo
        addFormLabel("Service:", gbc, formPanel, 0, row);
        serviceCombo = new JComboBox<>();
        UIStyleManager.styleComboBox(serviceCombo);
        loadServices();
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(serviceCombo, gbc);

        // Date d'embauche
        addFormLabel("Date d'embauche*:", gbc, formPanel, 0, row);
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(Date.valueOf(LocalDate.now()));
        gbc.gridx = 1;
        gbc.gridy = row++;
        formPanel.add(dateSpinner, gbc);

        // Salaire de base
        addFormLabel("Salaire de base* (€):", gbc, formPanel, 0, row);
        salaireField = addFormTextField(gbc, formPanel, 1, row++, 15);

        // New fields
        addFormLabel("CIN:", gbc, formPanel, 0, row);
        cinField = addFormTextField(gbc, formPanel, 1, row++, 25);

        addFormLabel("CNSS:", gbc, formPanel, 0, row);
        cnssField = addFormTextField(gbc, formPanel, 1, row++, 25);

        addFormLabel("Téléphone:", gbc, formPanel, 0, row);
        telephoneField = addFormTextField(gbc, formPanel, 1, row++, 25);

        addFormLabel("Email:", gbc, formPanel, 0, row);
        emailField = addFormTextField(gbc, formPanel, 1, row++, 25);

        addFormLabel("Adresse:", gbc, formPanel, 0, row);
        adresseField = addFormTextField(gbc, formPanel, 1, row++, 25);

        addFormLabel("Statut:", gbc, formPanel, 0, row);
        statutField = addFormTextField(gbc, formPanel, 1, row++, 25);

        addFormLabel("Type de Contrat:", gbc, formPanel, 0, row);
        typeContratField = addFormTextField(gbc, formPanel, 1, row++, 25);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        UIStyleManager.stylePanel(buttonPanel);

        if (employe.getIdEmploye() != 0) {
            exportButton = new JButton("Exporter Fiche");
            UIStyleManager.styleButton(exportButton);
            exportButton.addActionListener(this::exportEmployeeDetails);
            buttonPanel.add(exportButton);
        }

        JButton saveButton = new JButton("Enregistrer");
        UIStyleManager.styleButton(saveButton);
        saveButton.addActionListener(this::saveEmploye);
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Annuler");
        UIStyleManager.styleButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void addFormLabel(String text, GridBagConstraints gbc, JPanel panel, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.1;
        JLabel label = new JLabel(text);
        UIStyleManager.styleLabel(label);
        panel.add(label, gbc);
    }

    private JTextField addFormTextField(GridBagConstraints gbc, JPanel panel, int x, int y, int columns) {
        JTextField field = new JTextField(columns);
        UIStyleManager.styleTextField(field);
        field.setMinimumSize(new Dimension(50, field.getPreferredSize().height));
        field.setPreferredSize(new Dimension(columns * 10, field.getPreferredSize().height));

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.9;
        panel.add(field, gbc);
        return field;
    }

    private void loadServices() {
        try {
            List<Service> services = serviceDAO.findAll();
            serviceCombo.removeAllItems();
            services.forEach(serviceCombo::addItem);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des services: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        if (employe.getIdEmploye() != 0) {
            nomField.setText(employe.getNom());
            prenomField.setText(employe.getPrenom());
            posteField.setText(employe.getPoste());
            salaireField.setText(String.format("%.2f", employe.getSalaireDeBase()));
            dateSpinner.setValue(Date.valueOf(employe.getDateEmbauche()));

            // Select the employee's service
            for (int i = 0; i < serviceCombo.getItemCount(); i++) {
                if (serviceCombo.getItemAt(i).getIdService() == employe.getServiceId()) {
                    serviceCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Populate new fields
            cinField.setText(employe.getCin());
            cnssField.setText(employe.getCnss());
            telephoneField.setText(employe.getTelephone());
            emailField.setText(employe.getEmail());
            adresseField.setText(employe.getAdresse());
            statutField.setText(employe.getStatut());
            typeContratField.setText(employe.getTypeContrat());
        }
    }

    private void saveEmploye(ActionEvent e) {
        try {
            // Validate required fields
            if (!ValidationUtil.validateRequiredFields(this,
                    new JComponent[]{nomField, prenomField, posteField, salaireField},
                    new String[]{"nom", "prénom", "poste", "salaire de base"})) {
                return;
            }

            // Validate salary format
            if (!ValidationUtil.isValidDouble(salaireField.getText())) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez entrer un salaire valide (ex: 2500.50)",
                        "Format invalide", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update employee object
            employe.setNom(nomField.getText().trim());
            employe.setPrenom(prenomField.getText().trim());
            employe.setPoste(posteField.getText().trim());

            Service selectedService = (Service) serviceCombo.getSelectedItem();
            if (selectedService != null) {
                employe.setServiceId(selectedService.getIdService());
            }

            java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
            employe.setDateEmbauche(utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

            employe.setSalaireDeBase(Double.parseDouble(salaireField.getText()));

            // Set values for new fields
            employe.setCin(cinField.getText().trim());
            employe.setCnss(cnssField.getText().trim());
            employe.setTelephone(telephoneField.getText().trim());
            employe.setEmail(emailField.getText().trim());
            employe.setAdresse(adresseField.getText().trim());
            employe.setStatut(statutField.getText().trim());
            employe.setTypeContrat(typeContratField.getText().trim());

            // Save to database
            employeDAO.save(employe);
            confirmed = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportEmployeeDetails(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter la fiche employé");
        fileChooser.setSelectedFile(new File(employe.getNom() + "_" + employe.getPrenom() + ".pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                PdfExporter.exportEmployeeDetails(employe, fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Fiche employé exportée avec succès!",
                        "Export réussi", JOptionPane.INFORMATION_MESSAGE);

                // Open the PDF automatically
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileChooser.getSelectedFile());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'export: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}