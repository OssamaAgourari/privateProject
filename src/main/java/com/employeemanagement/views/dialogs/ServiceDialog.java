package com.employeemanagement.views.dialogs;

import com.employeemanagement.dao.ServiceDAO;
import com.employeemanagement.models.Service;
import com.employeemanagement.utils.ValidationUtil;
import com.employeemanagement.utils.UIStyleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class ServiceDialog extends JDialog {
    private JTextField nomField;
    private JTextArea descriptionArea;
    private boolean confirmed = false;
    private Optional<Service> service;
    private final ServiceDAO serviceDAO;

    public ServiceDialog(JFrame parent, Optional<Service> service) {
        super(parent, service.isPresent() ? "Modifier Service" : "Ajouter Service", true);
        this.service = service;
        this.serviceDAO = new ServiceDAO();

        initializeComponents();
        service.ifPresent(s -> populateFields(s));
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        UIStyleManager.stylePanel(formPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Service name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Nom du service:");
        UIStyleManager.styleLabel(nameLabel);
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        nomField = new JTextField();
        UIStyleManager.styleTextField(nomField);
        formPanel.add(nomField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel descLabel = new JLabel("Description:");
        UIStyleManager.styleLabel(descLabel);
        formPanel.add(descLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UIStyleManager.NORMAL_FONT);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyleManager.stylePanel(buttonPanel);
        
        JButton saveButton = new JButton("Enregistrer");
        UIStyleManager.styleButton(saveButton);
        saveButton.addActionListener(e -> save());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Annuler");
        UIStyleManager.styleButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields(Service service) {
        nomField.setText(service.getNom());
        descriptionArea.setText(service.getDescription());
    }

    private void save() {
        try {
            validateFields();

            Service currentService = service.orElseGet(Service::new);
            currentService.setNom(nomField.getText().trim());
            currentService.setDescription(descriptionArea.getText().trim());

            serviceDAO.save(currentService);
            confirmed = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur d'enregistrement",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateFields() throws Exception {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            nomField.requestFocusInWindow();
            throw new Exception("Le nom du service est obligatoire");
        }

        if (!ValidationUtil.validateName(this, nomField, "Nom du service")) {
            throw new Exception("Le nom contient des caractères invalides");
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}