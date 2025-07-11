package com.employeemanagement.views;

import com.employeemanagement.dao.*;
import com.employeemanagement.models.*;
import com.employeemanagement.views.dialogs.*;
import com.employeemanagement.utils.*;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MainWindow extends JFrame {
    private final transient ServiceDAO serviceDAO;
    private final transient EmployeDAO employeDAO;
    private final transient SalaireDAO salaireDAO;
    private final transient PointageDAO pointageDAO;
    private final transient UtilisateurDAO utilisateurDAO;
    private final Utilisateur currentUser;

    public MainWindow(Utilisateur currentUser) {
        this.serviceDAO = new ServiceDAO();
        this.employeDAO = new EmployeDAO();
        this.salaireDAO = new SalaireDAO();
        this.pointageDAO = new PointageDAO();
        this.utilisateurDAO = new UtilisateurDAO();
        this.currentUser = currentUser;

        initializeWindow();
        initializeComponents();
    }

    private void initializeWindow() {
        setTitle("Gestion des Employés et Salaires - " + currentUser.getNomUtilisateur() +
                " (" + currentUser.getRole() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Try to load the icon, but don't fail if it's missing
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo.png"));
            if (icon.getIconWidth() > 0) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            // Log the error but continue without the icon
            AppLogger.getInstance().log(Level.WARNING, "Could not load application icon: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs based on user role
        if (!currentUser.getRole().equals("EMPLOYEE")) {
            tabbedPane.addTab("Employés", createEmployePanel());
            tabbedPane.addTab("Services", createServicePanel());
        }

        if (currentUser.getRole().equals("ADMIN")) {
            tabbedPane.addTab("Utilisateurs", createUserPanel());
        }

        tabbedPane.addTab("Salaires", createSalairePanel());
        tabbedPane.addTab("Pointage", createPointagePanel());

        add(tabbedPane, BorderLayout.CENTER);
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        UIStyleManager.styleMenuBar(menuBar);

        // File Menu
        JMenu fileMenu = new JMenu("Fichier");
        UIStyleManager.styleMenu(fileMenu);
        JMenuItem exitItem = new JMenuItem("Quitter");
        UIStyleManager.styleMenuItem(exitItem);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Aide");
        UIStyleManager.styleMenu(helpMenu);
        JMenuItem aboutItem = new JMenuItem("À propos");
        UIStyleManager.styleMenuItem(aboutItem);
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createEmployePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UIStyleManager.stylePanel(panel);
        String[] columns = {"ID", "Nom", "Prénom", "Poste", "Service", "Date Embauche", "Salaire Base", "Statut"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(createEmployeButtonPanel(table, model), BorderLayout.SOUTH);
        refreshEmployeTable(model);

        return panel;
    }

    private JPanel createEmployeButtonPanel(JTable table, DefaultTableModel model) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyleManager.stylePanel(buttonPanel);

        JButton addButton = new JButton("Ajouter");
        UIStyleManager.styleButton(addButton);
        addButton.addActionListener(e -> showEmployeDialog(null, model));
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Modifier");
        UIStyleManager.styleButton(editButton);
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int employeId = (Integer) model.getValueAt(selectedRow, 0);
                showEmployeDialog(employeId, model);
            } else {
                showMessage("Veuillez sélectionner un employé");
            }
        });
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Supprimer");
        UIStyleManager.styleButton(deleteButton);
        deleteButton.addActionListener(e -> deleteEmploye(table, model));
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("Actualiser");
        UIStyleManager.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshEmployeTable(model));
        buttonPanel.add(refreshButton);

        return buttonPanel;
    }

    private void showEmployeDialog(Integer employeId, DefaultTableModel model) {
        try {
            Optional<Employe> employe = employeId != null ? employeDAO.findById(employeId) : Optional.empty();
            EmployeDialog dialog = new EmployeDialog(this, employe);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                refreshEmployeTable(model);
            }
        } catch (Exception ex) {
            showError("Erreur: " + ex.getMessage());
        }
    }

    private void deleteEmploye(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int employeId = (Integer) model.getValueAt(selectedRow, 0);
            if (confirmAction("Êtes-vous sûr de vouloir supprimer cet employé?")) {
                try {
                    employeDAO.delete(employeId);
                    refreshEmployeTable(model);
                    showMessage("Employé supprimé avec succès");
                } catch (Exception ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            }
        } else {
            showMessage("Veuillez sélectionner un employé");
        }
    }

    private void refreshEmployeTable(DefaultTableModel model) {
        SwingWorker<List<Employe>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Employe> doInBackground() throws Exception {
                return employeDAO.findAll();
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<Employe> employes = get();
                    for (Employe emp : employes) {
                        model.addRow(new Object[]{
                                emp.getIdEmploye(),
                                emp.getNom(),
                                emp.getPrenom(),
                                emp.getPoste(),
                                emp.getServiceName(),
                                emp.getDateEmbauche(),
                                String.format("%.2f €", emp.getSalaireDeBase()),
                                emp.isActif() ? "Actif" : "Inactif"
                        });
                    }
                } catch (Exception e) {
                    AppLogger.getInstance().log(Level.SEVERE, "Error loading employees", e);
                    showError("Erreur lors du chargement: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ... [Similar SwingWorker implementations for other refresh methods]

    private JPanel createUserPanel() {
        if (!currentUser.getRole().equals("ADMIN")) {
            return new JPanel(new BorderLayout());
        }

        JPanel panel = new JPanel(new BorderLayout());
        UIStyleManager.stylePanel(panel);
        String[] columns = {"ID", "Nom d'utilisateur", "Rôle", "Employé associé"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        UIStyleManager.stylePanel(buttonPanel);

        JButton addButton = new JButton("Ajouter");
        UIStyleManager.styleButton(addButton);
        // Disable for RH and MANAGER roles
        if (currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            addButton.setEnabled(false);
        }
        addButton.addActionListener(e -> {
            UtilisateurDialog dialog = new UtilisateurDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                refreshUserTable(model);
            }
        });
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Modifier");
        UIStyleManager.styleButton(editButton);
        // Disable for RH and MANAGER roles
        if (currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            editButton.setEnabled(false);
        }
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (Integer) model.getValueAt(selectedRow, 0);
                try {
                    Optional<Utilisateur> user = utilisateurDAO.findById(userId);
                    user.ifPresent(u -> {
                        UtilisateurDialog dialog = new UtilisateurDialog(this, u);
                        dialog.setVisible(true);
                        if (dialog.isConfirmed()) {
                            refreshUserTable(model);
                        }
                    });
                } catch (Exception ex) {
                    showError("Erreur lors du chargement de l'utilisateur: " + ex.getMessage());
                }
            } else {
                showMessage("Veuillez sélectionner un utilisateur à modifier.");
            }
        });
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Supprimer");
        UIStyleManager.styleButton(deleteButton);
        // Disable for RH and MANAGER roles
        if (currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            deleteButton.setEnabled(false);
        }
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (Integer) model.getValueAt(selectedRow, 0);
                if (confirmAction("Êtes-vous sûr de vouloir supprimer cet utilisateur ?")) {
                    try {
                        utilisateurDAO.delete(userId);
                        refreshUserTable(model);
                        showMessage("Utilisateur supprimé avec succès.");
                    } catch (Exception ex) {
                        showError("Erreur lors de la suppression de l'utilisateur: " + ex.getMessage());
                    }
                }
            } else {
                showMessage("Veuillez sélectionner un utilisateur à supprimer.");
            }
        });
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("Actualiser");
        UIStyleManager.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshUserTable(model));
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        refreshUserTable(model);

        return panel;
    }

    private void refreshUserTable(DefaultTableModel model) {
        SwingWorker<List<Utilisateur>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Utilisateur> doInBackground() throws Exception {
                return utilisateurDAO.findAll();
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<Utilisateur> users = get();
                    for (Utilisateur user : users) {
                        model.addRow(new Object[]{
                                user.getIdUtilisateur(),
                                user.getNomUtilisateur(),
                                user.getRole(),
                                user.getEmployeNom()
                        });
                    }
                } catch (Exception e) {
                    AppLogger.getInstance().log(Level.SEVERE, "Error loading users", e);
                    showError("Erreur lors du chargement des utilisateurs: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UIStyleManager.stylePanel(panel);

        String[] columns = {"ID", "Nom", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        UIStyleManager.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        UIStyleManager.stylePanel(buttonPanel);

        JButton addButton = new JButton("Ajouter");
        UIStyleManager.styleButton(addButton);
        // Disable for RH and MANAGER roles
        if (currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            addButton.setEnabled(false);
        }
        addButton.addActionListener(e -> {
            ServiceDialog dialog = new ServiceDialog(this, Optional.empty());
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                refreshServiceTable(model);
            }
        });
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Modifier");
        UIStyleManager.styleButton(editButton);
        // Disable for RH and MANAGER roles
        if (currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            editButton.setEnabled(false);
        }
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int serviceId = (Integer) model.getValueAt(selectedRow, 0);
                try {
                    Optional<Service> service = serviceDAO.findById(serviceId);
                    if (service.isPresent()) {
                        ServiceDialog dialog = new ServiceDialog(this, service);
                        dialog.setVisible(true);
                        if (dialog.isConfirmed()) {
                            refreshServiceTable(model);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un service");
            }
        });
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Supprimer");
        UIStyleManager.styleButton(deleteButton);
        // Disable for RH and MANAGER roles
        if (currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            deleteButton.setEnabled(false);
        }
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int serviceId = (Integer) model.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer ce service?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        serviceDAO.delete(serviceId);
                        refreshServiceTable(model);
                        JOptionPane.showMessageDialog(this, "Service supprimé avec succès");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un service");
            }
        });
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("Actualiser");
        UIStyleManager.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshServiceTable(model));
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshServiceTable(model);

        return panel;
    }

    private void refreshServiceTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Service> services = serviceDAO.findAll();
            for (Service service : services) {
                if (service != null) {
                    model.addRow(new Object[]{
                            service.getIdService(),
                            service.getNom(),
                            service.getDescription()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage());
        }
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIStyleManager.styleTable(table);
        return table;
    }

    private boolean confirmAction(String message) {
        return JOptionPane.showConfirmDialog(this, message,
                "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Gestion des Employés v1.0\n© 2023 Votre Entreprise",
                "À propos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createSalairePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UIStyleManager.stylePanel(panel);

        String[] columns = {"ID", "Employé", "Mois", "Année", "Salaire Net"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = createTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        UIStyleManager.stylePanel(buttonPanel);

        JButton addButton = new JButton("Ajouter");
        UIStyleManager.styleButton(addButton);
        // Disable for EMPLOYEE, RH and MANAGER roles
        if (currentUser.getRole().equals("EMPLOYEE") || currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            addButton.setEnabled(false);
        }
        addButton.addActionListener(e -> {
            SalaireDialog dialog = new SalaireDialog(this, Optional.empty());
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                refreshSalaireTable(model);
            }
        });
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Modifier");
        UIStyleManager.styleButton(editButton);
        // Disable for EMPLOYEE, RH and MANAGER roles
        if (currentUser.getRole().equals("EMPLOYEE") || currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            editButton.setEnabled(false);
        }
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                try {
                    Optional<Salaire> salaire = salaireDAO.findById(id);
                    salaire.ifPresent(s -> {
                        SalaireDialog dialog = new SalaireDialog(this, Optional.of(s));
                        dialog.setVisible(true);
                        if (dialog.isConfirmed()) {
                            refreshSalaireTable(model);
                        }
                    });
                } catch (Exception ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            } else {
                showMessage("Veuillez sélectionner un salaire");
            }
        });
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Supprimer");
        UIStyleManager.styleButton(deleteButton);
        // Disable for EMPLOYEE, RH and MANAGER roles
        if (currentUser.getRole().equals("EMPLOYEE") || currentUser.getRole().equals("RH") || currentUser.getRole().equals("MANAGER")) {
            deleteButton.setEnabled(false);
        }
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                if (confirmAction("Confirmer la suppression de ce salaire ?")) {
                    try {
                        salaireDAO.delete(id);
                        refreshSalaireTable(model);
                        showMessage("Salaire supprimé avec succès");
                    } catch (Exception ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showMessage("Veuillez sélectionner un salaire");
            }
        });
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("Actualiser");
        UIStyleManager.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshSalaireTable(model));
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        refreshSalaireTable(model);

        return panel;
    }


    private void refreshSalaireTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        // Capture current user info for the SwingWorker thread
        final String userRole = currentUser.getRole();
        final int employeeId = currentUser.getIdEmploye(); // Get employee ID here

        SwingWorker<List<Salaire>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Salaire> doInBackground() throws Exception {
                List<Salaire> salaires;
                if (userRole.equals("EMPLOYEE")) {
                    // Use the captured employeeId
                    salaires = salaireDAO.findByEmployeeId(employeeId);
                } else {
                    // Fetch all salaries for other roles
                    salaires = salaireDAO.findAll();
                }
                return salaires;
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<Salaire> salaires = get();
                    for (Salaire salaire : salaires) {
                        if (salaire != null) {
                            model.addRow(new Object[]{
                                    salaire.getIdSalaire(),
                                    salaire.getEmployeNom(),
                                    salaire.getMois(),
                                    salaire.getAnnee(),
                                    String.format("%.2f €", salaire.getStoredSalaireNet())
                            });
                        }
                    }
                } catch (Exception e) {
                    AppLogger.getInstance().log(Level.SEVERE, "Error loading salaries", e);
                    showError("Erreur lors du chargement des salaires: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }


    private JPanel createPointagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UIStyleManager.stylePanel(panel);

        String[] columns = {"ID", "Employé", "Date", "Heure d'entrée", "Heure de départ"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = createTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        UIStyleManager.stylePanel(buttonPanel);

        JButton addButton = new JButton("Ajouter");
        UIStyleManager.styleButton(addButton);
        if (!currentUser.getRole().equals("EMPLOYEE")) {
            addButton.setEnabled(false);
        }
        addButton.addActionListener(e -> {
            PointageDialog dialog = new PointageDialog(this, Optional.empty());
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                refreshPointageTable(model);
            }
        });
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Modifier");
        UIStyleManager.styleButton(editButton);
        if (!currentUser.getRole().equals("EMPLOYEE")) {
            editButton.setEnabled(false);
        }
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                Optional<Pointage> pointage = Optional.empty();
                try {
                    pointage = pointageDAO.findById(id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                pointage.ifPresent(p -> {
                    JDialog genericDialog = new PointageDialog((Frame) this, Optional.of(p));
                    PointageDialog dialog = (PointageDialog) genericDialog;
                    dialog.setVisible(true);
                    if (dialog.isConfirmed()) {
                        refreshPointageTable(model);
                    }
                });
            } else {
                showMessage("Veuillez sélectionner un pointage");
            }
        });
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Supprimer");
        UIStyleManager.styleButton(deleteButton);
        if (currentUser.getRole().equals("EMPLOYEE")) {
            deleteButton.setEnabled(false);
        }
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                if (confirmAction("Confirmer la suppression de ce pointage ?")) {
                    try {
                        pointageDAO.delete(id);
                        refreshPointageTable(model);
                        showMessage("Pointage supprimé avec succès");
                    } catch (Exception ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                }
            } else {
                showMessage("Veuillez sélectionner un pointage");
            }
        });
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("Actualiser");
        UIStyleManager.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshPointageTable(model));
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        refreshPointageTable(model);

        return panel;
    }


    private void refreshPointageTable(DefaultTableModel model) {
        model.setRowCount(0);

        // Capture current user info for the SwingWorker thread
        final String userRole = currentUser.getRole();
        final int employeeId = currentUser.getIdEmploye(); // Get employee ID here

        SwingWorker<List<Pointage>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Pointage> doInBackground() throws Exception {
                List<Pointage> pointages;
                if (userRole.equals("EMPLOYEE")) {
                    // Use the captured employeeId
                    pointages = pointageDAO.findByEmployeeId(employeeId);
                } else {
                    // Fetch all pointages for other roles
                    pointages = pointageDAO.findAll();
                }
                return pointages;
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<Pointage> pointages = get();
                    for (Pointage p : pointages) {
                        model.addRow(new Object[]{
                                p.getIdPointage(),
                                p.getEmployeNom(),
                                p.getDate(),
                                p.getHeureArrivee(),
                                p.getHeureDepart()
                        });
                    }
                } catch (Exception e) {
                    AppLogger.getInstance().log(Level.SEVERE, "Error loading pointages", e);
                    showError("Erreur lors du chargement des pointages: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

}
