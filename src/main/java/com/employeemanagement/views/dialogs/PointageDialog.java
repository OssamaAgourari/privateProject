package com.employeemanagement.views.dialogs;

import com.employeemanagement.dao.EmployeDAO;
import com.employeemanagement.dao.PointageDAO;
import com.employeemanagement.models.Employe;
import com.employeemanagement.models.Pointage;
import com.employeemanagement.utils.DateUtils;
import com.employeemanagement.utils.UIStyleManager;
import com.employeemanagement.utils.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Dialog for managing employee time tracking records
 */
public class PointageDialog extends JDialog {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private boolean confirmed = false;

    // Form components
    private JComboBox<Employe> employeCombo;
    private JSpinner dateSpinner;
    private JSpinner heureArriveeSpinner;
    private JSpinner heureDepartSpinner;

    // DAOs
    private final EmployeDAO employeDAO;
    private final PointageDAO pointageDAO;
    private Pointage pointage;

    public PointageDialog(Frame parent, Optional<Pointage> pointageOpt) {
        super(parent, pointageOpt.isPresent() ? "Modifier Pointage" : "Ajouter Pointage", true);
        this.pointage = pointageOpt.orElse(new Pointage());
        this.employeDAO = new EmployeDAO();
        this.pointageDAO = new PointageDAO();
        initializeComponents();

        if (pointageOpt.isPresent()) {
            populateFields(pointageOpt.get());
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
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // Increased gaps
        UIStyleManager.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Increased padding

        JLabel employeLabel = new JLabel("Employé:");
        UIStyleManager.styleLabel(employeLabel);
        formPanel.add(employeLabel);
        employeCombo = new JComboBox<>();
        UIStyleManager.styleComboBox(employeCombo);
        employeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Employe) {
                    Employe emp = (Employe) value;
                    setText(emp.getNom() + " " + emp.getPrenom());
                } else if (value == null) {
                    setText("Sélectionner un employé");
                }
                 if (!isSelected) {
                    c.setBackground(UIStyleManager.BACKGROUND_COLOR); // Consistent background
                }
                return c;
            }
        });
        loadEmployes();
        formPanel.add(employeCombo);

        JLabel dateLabel = new JLabel("Date:");
        UIStyleManager.styleLabel(dateLabel);
        formPanel.add(dateLabel);
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        // UIStyleManager.styleSpinner(dateSpinner);
        formPanel.add(dateSpinner);

        JLabel heureArriveeLabel = new JLabel("Heure d'arrivée:");
        UIStyleManager.styleLabel(heureArriveeLabel);
        formPanel.add(heureArriveeLabel);
        SpinnerDateModel heureArriveeModel = new SpinnerDateModel();
        heureArriveeSpinner = new JSpinner(heureArriveeModel);
        JSpinner.DateEditor heureArriveeEditor = new JSpinner.DateEditor(heureArriveeSpinner, "HH:mm");
        heureArriveeSpinner.setEditor(heureArriveeEditor);
        // UIStyleManager.styleSpinner(heureArriveeSpinner);
        formPanel.add(heureArriveeSpinner);

        JLabel heureDepartLabel = new JLabel("Heure de départ:");
        UIStyleManager.styleLabel(heureDepartLabel);
        formPanel.add(heureDepartLabel);
        SpinnerDateModel heureDepartModel = new SpinnerDateModel();
        heureDepartSpinner = new JSpinner(heureDepartModel);
        JSpinner.DateEditor heureDepartEditor = new JSpinner.DateEditor(heureDepartSpinner, "HH:mm");
        heureDepartSpinner.setEditor(heureDepartEditor);
        // UIStyleManager.styleSpinner(heureDepartSpinner);
        formPanel.add(heureDepartSpinner);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); // Increased gaps
        UIStyleManager.stylePanel(buttonPanel);

        JButton saveButton = new JButton("Enregistrer");
        UIStyleManager.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            if (employeCombo.getSelectedItem() == null) {
                 JOptionPane.showMessageDialog(this, "Veuillez sélectionner un employé.");
                 return;
            }

            try {
                // Get Pointage object from form fields
                Pointage pointageToSave = getPointage();

                // Save to database
                pointageDAO.save(pointageToSave);
                confirmed = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'enregistrement du pointage: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton cancelButton = new JButton("Annuler");
        UIStyleManager.styleButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
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

    private void populateFields(Pointage pointage) {
        // Populate fields from the Pointage object
        // Assuming Employe is correctly represented in the ComboBox
        for (int i = 0; i < employeCombo.getItemCount(); i++) {
            if (employeCombo.getItemAt(i).getIdEmploye() == pointage.getEmployeId()) {
                employeCombo.setSelectedIndex(i);
                break;
            }
        }
        // Convert LocalDate to Date for the spinner
        dateSpinner.setValue(Date.from(pointage.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        // Convert LocalTime to Date for the spinners - need to combine with a date
        LocalDate pointageDate = pointage.getDate();
        heureArriveeSpinner.setValue(Date.from(pointageDate.atTime(pointage.getHeureArrivee()).atZone(ZoneId.systemDefault()).toInstant()));
        // Check if heureDepart is not null before setting
        if (pointage.getHeureDepart() != null) {
             heureDepartSpinner.setValue(Date.from(pointageDate.atTime(pointage.getHeureDepart()).atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    private Pointage getPointage() {
        Pointage currentPointage = pointage;

        Employe selectedEmploye = (Employe) employeCombo.getSelectedItem();
        if (selectedEmploye != null) {
            currentPointage.setEmployeId(selectedEmploye.getIdEmploye());
            currentPointage.setEmployeNom(selectedEmploye.getNom() + " " + selectedEmploye.getPrenom());
        }

        // Convert Date from spinner to LocalDate
        Date date = (Date) dateSpinner.getValue();
        currentPointage.setDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // Convert Date from spinner to LocalTime
        Date heureArrivee = (Date) heureArriveeSpinner.getValue();
        currentPointage.setHeureArrivee(heureArrivee.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());

        Date heureDepart = (Date) heureDepartSpinner.getValue();
         // Set heureDepart only if it's a valid time (not the default spinner date)
        // Need to check if the time component is non-zero, as Date() defaults to epoch
        LocalTime heureDepartLocalTime = heureDepart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        if (heureDepart != null && heureDepartLocalTime.toSecondOfDay() != 0) { // Check if time is not midnight epoch
             currentPointage.setHeureDepart(heureDepartLocalTime);
        } else {
             currentPointage.setHeureDepart(null); // Set to null if no departure time is entered or it's epoch time
        }

        return currentPointage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}