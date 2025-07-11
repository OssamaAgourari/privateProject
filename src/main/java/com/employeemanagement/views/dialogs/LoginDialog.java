package com.employeemanagement.views.dialogs;

import com.employeemanagement.dao.UtilisateurDAO;
import com.employeemanagement.models.Utilisateur;
import com.employeemanagement.utils.PasswordUtil;
import com.employeemanagement.utils.UIStyleManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Secure authentication dialog with multiple login attempts and password policy enforcement
 */
public final class LoginDialog extends JDialog {
    private static final int MAX_ATTEMPTS = 3;
    private static final int SESSION_TIMEOUT_MIN = 5;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AtomicInteger attemptsRemaining;
    private boolean authenticated = false;
    private Optional<Utilisateur> currentUser = Optional.empty();
    private Timer inactivityTimer;
    private JLabel attemptsRemainingLabel;

    public LoginDialog(JFrame parent) {
        super(parent, "Connexion", true);
        this.usernameField = new JTextField(25);
        this.passwordField = new JPasswordField(25);
        this.attemptsRemaining = new AtomicInteger(MAX_ATTEMPTS);

        configureDialog();
        initializeComponents();
        setupInactivityTimer();

        // Add these lines to ensure proper display
        pack();
        setLocationRelativeTo(parent);
    }

    private void configureDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void initializeComponents() {
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        UIStyleManager.stylePanel(mainPanel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        UIStyleManager.stylePanel(formPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.LINE_END;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        UIStyleManager.styleLabel(usernameLabel);
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        UIStyleManager.styleTextField(usernameField);
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel passwordLabel = new JLabel("Mot de passe:");
        UIStyleManager.styleLabel(passwordLabel);
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        UIStyleManager.styleTextField(passwordField);
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        UIStyleManager.stylePanel(buttonPanel);
        JButton loginButton = createLoginButton();
        JButton cancelButton = createCancelButton();

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        UIStyleManager.stylePanel(infoPanel);
        attemptsRemainingLabel = new JLabel("<html><i>Essais restants: " + MAX_ATTEMPTS + "</i></html>");
        UIStyleManager.styleLabel(attemptsRemainingLabel);
        infoPanel.add(attemptsRemainingLabel, BorderLayout.EAST);
        
        JLabel defaultUserLabel = new JLabel("<html><center>Utilisateur par défaut:<br>admin / Abdo@1234</center></html>");
        UIStyleManager.styleLabel(defaultUserLabel);
        infoPanel.add(defaultUserLabel, BorderLayout.CENTER);

        // Assembly
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Keyboard focus
        getRootPane().setDefaultButton(loginButton);
        usernameField.requestFocusInWindow();
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Connexion");
        UIStyleManager.styleButton(button);
        button.addActionListener(e -> authenticate());
        return button;
    }

    private JButton createCancelButton() {
        JButton button = new JButton("Annuler");
        UIStyleManager.styleButton(button);
        button.addActionListener(e -> dispose());
        return button;
    }

    private void setupInactivityTimer() {
        inactivityTimer = new Timer(SESSION_TIMEOUT_MIN * 60 * 1000, e -> {
            JOptionPane.showMessageDialog(this,
                    "Session expirée pour inactivité",
                    "Déconnexion", JOptionPane.WARNING_MESSAGE);
            cancelLogin();
        });
        inactivityTimer.setRepeats(false);
        inactivityTimer.start();
    }

    private void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.restart();
        }
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (!validateInput(username, password)) {
            return;
        }

        try {
            UtilisateurDAO userDAO = new UtilisateurDAO();
            System.out.println("LoginDialog.authenticate - Plain password string: " + new String(password));
            Optional<Utilisateur> user = userDAO.authenticate(username, new String(password));

            if (user.isPresent()) {
                handleSuccessfulLogin(user.get());
            } else {
                handleFailedLogin();
            }
        } catch (Exception e) {
            handleSystemError(e);
        } finally {
            clearPasswordField();
        }
    }

    private boolean validateInput(String username, char[] password) {
        if (username.isEmpty() || password.length == 0) {
            showWarning("Veuillez saisir un nom d'utilisateur et un mot de passe", "Champs requis");
            return false;
        }
        return true;
    }

    private void handleSuccessfulLogin(Utilisateur user) {
        currentUser = Optional.of(user);
        authenticated = true;
        inactivityTimer.stop();
        dispose();
    }

    private void handleFailedLogin() {
        attemptsRemaining.decrementAndGet();
        attemptsRemainingLabel.setText("<html><i>Essais restants: " + attemptsRemaining.get() + "</i></html>");

        if (attemptsRemaining.get() > 0) {
            showWarning(String.format(
                    "Nom d'utilisateur ou mot de passe incorrect (%d essais restants)",
                    attemptsRemaining.get()), "Authentification échouée");
            passwordField.setText("");
            usernameField.requestFocus();
        } else {
            showError("Nombre maximum de tentatives atteint", "Accès refusé");
            cancelLogin();
        }
    }

    private void handleSystemError(Exception e) {
        showError("Erreur de connexion: " + e.getMessage(), "Erreur système");
        e.printStackTrace();
    }

    private void clearPasswordField() {
        passwordField.setText("");
        Arrays.fill(passwordField.getPassword(), '\0');
    }

    private void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void cancelLogin() {
        authenticated = false;
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
        dispose();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Optional<Utilisateur> getCurrentUser() {
        return currentUser;
    }

    @Override
    public void dispose() {
        clearPasswordField();
        super.dispose();
    }
}