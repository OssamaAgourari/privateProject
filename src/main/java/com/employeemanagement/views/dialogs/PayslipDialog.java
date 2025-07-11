package com.employeemanagement.views.dialogs;

import com.employeemanagement.dao.SalaireDAO;
import com.employeemanagement.models.Salaire;
import com.employeemanagement.utils.PdfExporter;
import com.employeemanagement.utils.UIStyleManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.text.DecimalFormat;

/**
 * Dialog for displaying and exporting employee payslips
 */
public class PayslipDialog extends JDialog {
    private final Salaire salaire;
    private final SalaireDAO salaireDAO = new SalaireDAO();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00 €");

    public PayslipDialog(JFrame parent, Salaire salaire) {
        super(parent, "Fiche de Paie - " + salaire.getEmployeNom(), true);
        this.salaire = salaire;
        initializeComponents();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Create payslip content
        JTextArea payslipArea = createPayslipTextArea();
        JScrollPane scrollPane = new JScrollPane(payslipArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(payslipArea);
        UIStyleManager.stylePanel(buttonPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextArea createPayslipTextArea() {
        JTextArea payslipArea = new JTextArea();
        payslipArea.setEditable(false);
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        payslipArea.setText(generatePayslipText());
        UIStyleManager.styleTextArea(payslipArea);
        return payslipArea;
    }

    private String generatePayslipText() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("            FICHE DE PAIE\n");
        sb.append("========================================\n\n");
        sb.append(String.format("Employé:    %s\n", salaire.getEmployeNom()));
        sb.append(String.format("ID Employé:  %s\n", salaire.getEmployeId()));
        sb.append(String.format("Période:    %s %d\n\n", salaire.getMoisName(), salaire.getAnnee()));

        sb.append("----------------------------------------\n");
        sb.append(String.format("Salaire de base:        %12s\n", formatCurrency(salaire.getMontant())));
        sb.append(String.format("Heures supplémentaires: %12s\n", formatCurrency(salaire.getHeuresSupplementaires())));
        sb.append(String.format("Primes:                 %12s\n", formatCurrency(salaire.getPrimes())));
        sb.append(String.format("Avantages:              %12s\n", formatCurrency(salaire.getAvantages())));
        sb.append(String.format("Retenues:               %12s\n", formatCurrency(salaire.getRetenues())));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Salaire brut:           %12s\n", formatCurrency(salaire.getSalaireBrut())));
        sb.append(String.format("Cotisations:            %12s\n", formatCurrency(salaire.getCotisations())));
        sb.append(String.format("Salaire net:            %12s\n", formatCurrency(salaire.getSalaireNet())));
        sb.append("========================================\n");
        return sb.toString();
    }

    private String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    private JPanel createButtonPanel(JTextArea payslipArea) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton printButton = new JButton("Imprimer");
        UIStyleManager.styleButton(printButton);
        printButton.addActionListener(e -> printPayslip(payslipArea));

        JButton exportPdfButton = new JButton("Exporter PDF");
        UIStyleManager.styleButton(exportPdfButton);
        exportPdfButton.addActionListener(e -> exportPayslipPdf());

        JButton closeButton = new JButton("Fermer");
        UIStyleManager.styleButton(closeButton);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(exportPdfButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private void printPayslip(JTextArea payslipArea) {
        try {
            boolean complete = payslipArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this,
                        "Impression terminée avec succès",
                        "Impression", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Impression annulée",
                        "Impression", JOptionPane.WARNING_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur d'impression: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportPayslipPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer la fiche de paie");
        fileChooser.setSelectedFile(new File(
                "Fiche_Paie_" + salaire.getEmployeNom() + "_" +
                        salaire.getMois() + "_" + salaire.getAnnee() + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                PdfExporter.exportPayslip(salaire, fileToSave.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Fiche de paie exportée avec succès:\n" + fileToSave.getAbsolutePath(),
                        "Export réussi", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'export PDF: " + ex.getMessage(),
                        "Erreur d'export", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}