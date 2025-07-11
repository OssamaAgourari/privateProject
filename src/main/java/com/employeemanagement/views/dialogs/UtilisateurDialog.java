package com.employeemanagement.views.dialogs;

import com.employeemanagement.dao.EmployeDAO;
import com.employeemanagement.dao.UtilisateurDAO;
import com.employeemanagement.models.Employe;
import com.employeemanagement.models.Utilisateur;
import com.employeemanagement.utils.PasswordUtil;
import com.employeemanagement.utils.UIStyleManager;
import com.employeemanagement.utils.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

public class UtilisateurDialog extends JDialog {
    private Utilisateur user;
    private boolean confirmed = false;

    // Form components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JComboBox<Employe> employeCombo;

    // DAOs
    private UtilisateurDAO utilisateurDAO;
    private EmployeDAO employeDAO;

    public UtilisateurDialog(JFrame parent, Utilisateur user) {
        super(parent, user != null ? "Modifier Utilisateur" : "Ajouter Utilisateur", true);
        this.user = user;
        this.utilisateurDAO = new UtilisateurDAO();
        this.employeDAO = new EmployeDAO();
        initializeComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        UIStyleManager.stylePanel((JPanel) this.getContentPane()); // Style the content pane
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // Increased gaps
        UIStyleManager.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Increased padding

        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        UIStyleManager.styleLabel(usernameLabel);
        formPanel.add(usernameLabel);
        usernameField = new JTextField(20);
        UIStyleManager.styleTextField(usernameField);
        formPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Mot de passe:");
        UIStyleManager.styleLabel(passwordLabel);
        formPanel.add(passwordLabel);
        passwordField = new JPasswordField(20);
        UIStyleManager.styleTextField(passwordField);
        formPanel.add(passwordField);

        JLabel roleLabel = new JLabel("Rôle:");
        UIStyleManager.styleLabel(roleLabel);
        formPanel.add(roleLabel);
        roleCombo = new JComboBox<>(new String[]{"ADMIN", "RH", "MANAGER", "EMPLOYEE"});
        UIStyleManager.styleComboBox(roleCombo);
        formPanel.add(roleCombo);

        JLabel employeLabel = new JLabel("Employé:");
        UIStyleManager.styleLabel(employeLabel);
        formPanel.add(employeLabel);
        employeCombo = new JComboBox<>();
        UIStyleManager.styleComboBox(employeCombo);
        loadEmployes();
        formPanel.add(employeCombo);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); // Increased gaps
        UIStyleManager.stylePanel(buttonPanel);
        
        JButton saveButton = new JButton("Enregistrer");
        UIStyleManager.styleButton(saveButton);
        saveButton.addActionListener(e -> saveUser());
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

    private void populateFields() {
        if (user != null) {
            usernameField.setText(user.getNomUtilisateur());
            // Note: We don't populate the password field for security reasons
            roleCombo.setSelectedItem(user.getRole());
            // Select the corresponding employee in the combo box
            for (int i = 0; i < employeCombo.getItemCount(); i++) {
                Employe emp = employeCombo.getItemAt(i);
                if (emp != null && user.getIdEmploye() == emp.getIdEmploye()) {
                    employeCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean validateInput() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom d'utilisateur est obligatoire.", "Validation", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocusInWindow();
            return false;
        }

        // Only validate password if it's a new user or password field is not empty
        if (user == null || password.length > 0) {
             if (!ValidationUtil.validatePassword(this, passwordField, "Mot de passe", 8, true)) {
                return false;
             }
        }

        if (employeCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un employé.", "Validation", JOptionPane.WARNING_MESSAGE);
            employeCombo.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private void saveUser() {
        if (validateInput()) {
            try {
                String username = usernameField.getText().trim();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                String role = (String) roleCombo.getSelectedItem();
                Employe selectedEmploye = (Employe) employeCombo.getSelectedItem();

                if (user == null) {
                    // Create new user
                    Utilisateur newUser = new Utilisateur();
                    newUser.setNomUtilisateur(username);
                    newUser.setMotDePasse(PasswordUtil.hashPassword(password));
                    newUser.setRole(role);
                    newUser.setIdEmploye(selectedEmploye.getIdEmploye());
                    utilisateurDAO.save(newUser);
                } else {
                    // Update existing user
                    user.setNomUtilisateur(username);
                    if (password.length() > 0) {
                         user.setMotDePasse(PasswordUtil.hashPassword(password));
                    }
                    user.setRole(role);
                    user.setIdEmploye(selectedEmploye.getIdEmploye());
                    utilisateurDAO.update(user);
                }

                confirmed = true;
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'enregistrement de l'utilisateur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Print stack trace for debugging
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
} 