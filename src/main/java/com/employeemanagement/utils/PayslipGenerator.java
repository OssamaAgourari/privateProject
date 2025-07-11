package com.employeemanagement.utils;

import com.employeemanagement.models.Employe;
import com.employeemanagement.models.Salaire;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utility class for generating PDF payslips
 */
public class PayslipGenerator {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRANCE);

    /**
     * Generates a PDF payslip for an employee
     * @param employe The employee
     * @param salaire The salary record
     * @param outputPath The path where to save the PDF
     * @throws Exception if there's an error generating the PDF
     */
    public static void generatePayslip(Employe employe, Salaire salaire, String outputPath) throws Exception { // Create a new document
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputPath)); // Initialize PDF writer
        document.open();

        // Add title
        Paragraph title = new Paragraph("FICHE DE PAIE", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add employee information
        document.add(createEmployeeInfoTable(employe));
        document.add(Paragraph.getInstance("\n"));

        // Add salary period
        Paragraph period = new Paragraph(
            String.format("Période : %s %d", salaire.getMoisName(), salaire.getAnnee()),
            HEADER_FONT
        );
        period.setSpacingAfter(10);
        document.add(period);

        // Add salary details
        document.add(createSalaryDetailsTable(salaire));
        document.add(Paragraph.getInstance("\n"));

        // Add signature section
        document.add(createSignatureSection());

        document.close();
    }

    private static PdfPTable createEmployeeInfoTable(Employe employe) throws DocumentException { // Create a table for employee information
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        addTableRow(table, "Nom et Prénom", employe.getFullName());
        addTableRow(table, "Matricule", String.valueOf(employe.getIdEmploye()));
        addTableRow(table, "Service", employe.getServiceName());
        addTableRow(table, "Poste", employe.getPoste());
        addTableRow(table, "Date d'embauche",
            employe.getDateEmbauche() != null ? employe.getDateEmbauche().format(DATE_FORMATTER) : "N/A");

        // Add new fields
        addTableRow(table, "Date de Naissance",
            employe.getDateNaissance() != null ? employe.getDateNaissance().format(DATE_FORMATTER) : "N/A");
        addTableRow(table, "CIN", employe.getCin() != null ? employe.getCin() : "N/A");
        addTableRow(table, "CNSS", employe.getCnss() != null ? employe.getCnss() : "N/A");
        addTableRow(table, "Téléphone", employe.getTelephone() != null ? employe.getTelephone() : "N/A");
        addTableRow(table, "Email", employe.getEmail() != null ? employe.getEmail() : "N/A");
        addTableRow(table, "Adresse", employe.getAdresse() != null ? employe.getAdresse() : "N/A");
        addTableRow(table, "Statut", employe.getStatut() != null ? employe.getStatut() : "N/A");
        addTableRow(table, "Type de Contrat", employe.getTypeContrat() != null ? employe.getTypeContrat() : "N/A");

        return table;
    }

    private static PdfPTable createSalaryDetailsTable(Salaire salaire) throws DocumentException { // Create a table for salary details
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Earnings
        addTableRow(table, "Salaire de base", SalaryCalculator.formatSalary(salaire.getMontant()));
        if (salaire.getHeuresSupplementaires() > 0) {
            addTableRow(table, "Heures supplémentaires", 
                SalaryCalculator.formatSalary(salaire.getHeuresSupplementaires() * 
                salaire.getTauxHeuresSupplementaires() * 1.25));
        }
        if (salaire.getPrimes() > 0) {
            addTableRow(table, "Primes", SalaryCalculator.formatSalary(salaire.getPrimes()));
        }
        if (salaire.getAvantages() > 0) {
            addTableRow(table, "Avantages", SalaryCalculator.formatSalary(salaire.getAvantages()));
        }

        // Add a separator
        addTableRow(table, "Salaire brut", SalaryCalculator.formatSalary(salaire.getSalaireBrut()));

        // Deductions
        if (salaire.getCotisations() > 0) {
            addTableRow(table, "Cotisations (CNSS + AMO)", 
                SalaryCalculator.formatSalary(salaire.getCotisations()));
        }
        if (salaire.getRetenues() > 0) {
            addTableRow(table, "Retenues", SalaryCalculator.formatSalary(salaire.getRetenues()));
        }

        // Net salary
        addTableRow(table, "Salaire net", SalaryCalculator.formatSalary(salaire.getSalaireNet()));

        return table;
    }

    private static PdfPTable createSignatureSection() throws DocumentException { // Create a table for signatures
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        PdfPCell cell = new PdfPCell(new Paragraph("Signature de l'employé", NORMAL_FONT));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Signature du responsable RH", NORMAL_FONT));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        return table;
    }

    private static void addTableRow(PdfPTable table, String label, String value) throws DocumentException { // Helper method to add a row to a table
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, NORMAL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Paragraph(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
} 