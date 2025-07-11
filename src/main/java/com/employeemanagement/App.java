package com.employeemanagement;

import com.employeemanagement.config.DatabaseConnection;
import com.employeemanagement.models.Utilisateur;
import com.employeemanagement.views.dialogs.LoginDialog;
import com.employeemanagement.views.MainWindow;
import com.employeemanagement.utils.AppLogger;
import com.employeemanagement.utils.UIStyleManager;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public final class App {
    private static final AppLogger logger = AppLogger.getInstance();
    private static final String LOGS_DIR = "logs";

    // Private constructor to prevent instantiation
    private App() {
        throw new AssertionError("This class should not be instantiated");
    }

    public static void main(String[] args) {
        // Temporary: Generate and print BCrypt hash for 'abdo1234'
        // System.out.println("BCrypt hash for 'abdo1234': " + com.employeemanagement.utils.PasswordUtil.hashPassword("abdo1234")); // Commented out temporary line

        configureEnvironment();
        showLoginAndLaunchApp();
    }

    private static void configureEnvironment() {
        // Add these system properties early
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        System.setProperty("sun.java2d.uiScale", "1.0"); // Fix HiDPI scaling

        configureLookAndFeel();
        configureGlobalExceptionHandling();
        createLogsDirectory();
        logger.log(Level.INFO, String.format(
                "Environnement initialisé - Java %s (%s) | Mémoire max: %d MB",
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                Runtime.getRuntime().maxMemory() / (1024 * 1024)
        ));
    }

    private static void createLogsDirectory() {
        try {
            if (!Files.exists(Paths.get(LOGS_DIR))) {
                Files.createDirectories(Paths.get(LOGS_DIR));
                logger.log(Level.CONFIG, "Dossier logs créé avec succès");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Échec de la création du dossier logs", e);
            showErrorDialog("Erreur Critique",
                    new RuntimeException("Impossible de créer le dossier de logs", e));
            System.exit(1);
        }
    }

    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Apply UIManager properties before creating components
            setUIManagerProperties();
            // Then apply our custom styles which might override some UIManager defaults
            UIStyleManager.applyStyle();
            logger.log(Level.CONFIG, "Look and feel système configuré");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Look and feel système non disponible", e);
            setDefaultLookAndFeel();
        }
    }

    private static void setUIManagerProperties() {
        UIManager.put("Button.showMnemonics", true);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(1, 1, 1, 1));
        UIManager.put("TabbedPane.tabsOverlapBorder", true);
        UIManager.put("Table.showGrid", true);
        UIManager.put("Table.gridColor", new Color(240, 240, 240));
        UIManager.put("Menu.foreground", UIStyleManager.TEXT_COLOR);
        UIManager.put("MenuItem.foreground", UIStyleManager.TEXT_COLOR);
        UIManager.put("MenuBar.background", UIStyleManager.BACKGROUND_COLOR);
        UIManager.put("TableHeader.foreground", UIStyleManager.TEXT_COLOR);
        UIManager.put("TableHeader.background", UIStyleManager.TABLE_HEADER_COLOR);

        // Better font rendering
        UIManager.put("Text.aaTextInfo", System.getProperty("os.name").contains("Mac") ? 1 : 0);
    }

    private static void setDefaultLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            logger.log(Level.CONFIG, "Look and feel par défaut configuré");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Échec de la configuration du look and feel", ex);
        }
    }

    private static void configureGlobalExceptionHandling() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.log(Level.SEVERE,
                    String.format("Exception non capturée dans %s [%s]",
                            thread.getName(), thread.getId()),
                    throwable);
            showErrorDialog("Erreur Critique", throwable);
        });
    }

    private static void showLoginAndLaunchApp() {
        EventQueue.invokeLater(() -> {
            int attempts = 0;
            final int MAX_ATTEMPTS = 3;

            while (attempts < MAX_ATTEMPTS) {
                LoginDialog loginDialog = new LoginDialog(null);
                centerWindow(loginDialog);
                loginDialog.setVisible(true);

                if (loginDialog.isAuthenticated()) {
                    loginDialog.getCurrentUser().ifPresentOrElse(
                            App::launchMainApplication,
                            () -> handleAuthFailure()
                    );
                    return;
                } else {
                    attempts++;
                    if (attempts < MAX_ATTEMPTS) {
                        int choice = JOptionPane.showConfirmDialog(
                                null,
                                "Voulez-vous réessayer?",
                                "Authentification annulée",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (choice != JOptionPane.YES_OPTION) {
                            break;
                        }
                    }
                }
            }
            handleAuthCancellation();
        });
    }

    private static void handleAuthFailure() {
        logger.log(Level.WARNING, "Aucun utilisateur retourné malgré l'authentification");
        showErrorDialog("Échec d'Authentification",
                new RuntimeException("Erreur interne d'authentification"));
        System.exit(1);
    }

    private static void handleAuthCancellation() {
        logger.log(Level.INFO, "Authentification annulée après plusieurs tentatives");
        System.exit(0);  // Consider using Platform.exit() for JavaFX apps
    }

    private static void shutdown() {
        try {
            // Clean up resources
            DatabaseConnection.shutdown();
            logger.log(Level.INFO, "Application fermée proprement");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la fermeture", e);
        } finally {
            System.exit(0);
        }
    }

    private static void launchMainApplication(Utilisateur currentUser) {
        try {
            MainWindow mainWindow = new MainWindow(currentUser);
            centerWindow(mainWindow);
            mainWindow.setVisible(true);
            logger.log(Level.INFO,
                    String.format("Application lancée pour %s (Rôle: %s)",
                            currentUser.getNomUtilisateur(),
                            currentUser.getRole()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Échec du lancement de l'application", e);
            showErrorDialog("Échec du Démarrage", e);
            System.exit(1);
        }
    }

    private static void centerWindow(Window window) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();

        window.setLocation(
                (bounds.width - window.getWidth()) / 2 + bounds.x,
                (bounds.height - window.getHeight()) / 2 + bounds.y
        );
    }

    private static void showErrorDialog(String title, Throwable throwable) {
        String errorDetails = throwable.getMessage();
        if (errorDetails == null || errorDetails.isEmpty()) {
            errorDetails = "Aucun détail disponible";
        }

        JTextArea textArea = new JTextArea(
                "Erreur: " + errorDetails + "\n\n" +
                        "Classe: " + throwable.getClass().getSimpleName() + "\n" +
                        "Veuillez consulter les logs pour plus de détails."
        );

        textArea.setEditable(false);
        textArea.setBackground(UIManager.getColor("Panel.background"));

        JOptionPane.showMessageDialog(
                null,
                new Object[] {
                        "<html><b>" + title + "</b></html>",
                        new JScrollPane(textArea)
                },
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }


}