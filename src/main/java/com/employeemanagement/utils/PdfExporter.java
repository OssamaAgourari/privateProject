package com.employeemanagement.utils;

import com.employeemanagement.models.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Comprehensive PDF export utility for employee management system
 */
public class PdfExporter {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Font HIGHLIGHT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final BaseColor HEADER_BG_COLOR = new BaseColor(70, 130, 180);
    private static final BaseColor LIGHT_BG_COLOR = new BaseColor(240, 240, 240);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Exports employee list to PDF
     */
    public static void exportEmployees(List<Employe> employees, String filePath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();
        addDocumentHeader(writer, document, "Liste des Employés");
        addEmployeeTable(document, employees);
        addDocumentFooter(writer, document);
        document.close();
    }

    /**
     * Generates a payslip PDF
     */
    public static void exportPayslip(Salaire salaire, String filePath) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();
        addDocumentHeader(writer, document, "Fiche de Paie");
        addPayslipContent(document, salaire);
        addDocumentFooter(writer, document);
        document.close();
    }

    /**
     * Exports detailed employee profile
     */
    public static void exportEmployeeProfile(Employe employe, String filePath) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();
        addDocumentHeader(writer, document, "Fiche Employé - " + employe.getFullName());
        addEmployeeProfileContent(document, employe);
        addDocumentFooter(writer, document);
        document.close();
    }

    private static void addDocumentHeader(PdfWriter writer, Document document, String title) throws DocumentException, IOException {
        // Title
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20f);
        document.add(p);

        // Add metadata
        document.addAuthor("Employee Management System");
        document.addCreationDate();
        document.addCreator("EMS v1.0");

        // Add watermark for draft documents
        PdfContentByte canvas = writer.getDirectContentUnder();
        canvas.beginText();
        canvas.setFontAndSize(BaseFont.createFont(), 60);
        canvas.setColorFill(BaseColor.LIGHT_GRAY);
        canvas.showTextAligned(Element.ALIGN_CENTER, "EMS",
                document.getPageSize().getWidth()/2,
                document.getPageSize().getHeight()/2, 45);
        canvas.endText();
    }

    private static void addDocumentFooter(PdfWriter writer, Document document) throws DocumentException {
        PdfPTable footer = new PdfPTable(1);
        footer.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());

        Paragraph p = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER),
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
        p.setAlignment(Element.ALIGN_RIGHT);

        PdfPCell cell = new PdfPCell(p);
        cell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(cell);

        footer.writeSelectedRows(0, -1,
                document.leftMargin(),
                document.bottomMargin(),
                writer.getDirectContent());
    }

    private static void addEmployeeTable(Document document, List<Employe> employees) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Table headers
        String[] headers = {"ID", "Nom", "Prénom", "Poste", "Service", "Date Embauche"};
        for (String header : headers) {
            addHeaderCell(table, header);
        }

        // Table content
        boolean alternate = false;
        for (Employe emp : employees) {
            if (alternate) {
                table.getDefaultCell().setBackgroundColor(LIGHT_BG_COLOR);
            }

            addCell(table, String.valueOf(emp.getIdEmploye()));
            addCell(table, emp.getNom());
            addCell(table, emp.getPrenom());
            addCell(table, emp.getPoste());
            addCell(table, emp.getServiceName());
            addCell(table, emp.getDateEmbauche().format(DATE_FORMATTER));

            if (alternate) {
                table.getDefaultCell().setBackgroundColor(null);
            }
            alternate = !alternate;
        }

        document.add(table);
    }

    private static void addPayslipContent(Document document, Salaire salaire) throws DocumentException {
        // Employee information
        Paragraph info = new Paragraph();
        info.add(new Chunk("Employé: ", HIGHLIGHT_FONT));
        info.add(new Chunk(salaire.getEmployeNom(), NORMAL_FONT));
        info.add(Chunk.NEWLINE);
        info.add(new Chunk("Période: ", HIGHLIGHT_FONT));
        info.add(new Chunk(salaire.getMoisName() + " " + salaire.getAnnee(), NORMAL_FONT));
        info.setSpacingAfter(15f);
        document.add(info);

        // Salary details table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingAfter(15f);

        addSalaryRow(table, "Salaire de base:", formatCurrency(salaire.getMontant()));
        addSalaryRow(table, "Primes:", formatCurrency(salaire.getPrimes()));
        addSalaryRow(table, "Retenues:", "-" + formatCurrency(salaire.getRetenues()));

        // Separator
        PdfPCell separator = new PdfPCell(new Phrase(""));
        separator.setColspan(2);
        separator.setBorder(Rectangle.TOP);
        separator.setPaddingTop(5f);
        table.addCell(separator);

        // Net salary
        addSalaryRow(table, "Salaire Net:", formatCurrency(salaire.getSalaireNet()));

        document.add(table);

        // Legal mentions
        Paragraph legal = new Paragraph();
        legal.add(new Chunk("Signature:", HIGHLIGHT_FONT));
        legal.add(Chunk.NEWLINE);
        legal.add(new Chunk("Cachet de l'entreprise", NORMAL_FONT));
        legal.setSpacingBefore(20f);
        document.add(legal);
    }

    private static void addEmployeeProfileContent(Document document, Employe employe) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setSpacingBefore(10f);

        addProfileRow(table, "ID Employé:", String.valueOf(employe.getIdEmploye()));
        addProfileRow(table, "Nom Complet:", employe.getFullName());
        addProfileRow(table, "Poste:", employe.getPoste());
        addProfileRow(table, "Service:", employe.getServiceName());
        addProfileRow(table, "Date d'embauche:", employe.getDateEmbauche().format(DATE_FORMATTER));
        addProfileRow(table, "Ancienneté:", employe.getAnciennete() + " années");
        addProfileRow(table, "Salaire de base:", formatCurrency(employe.getSalaireDeBase()));

        document.add(table);
    }

    private static void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private static void addCell(PdfPTable table, String text) {
        table.addCell(new Phrase(text, NORMAL_FONT));
    }

    private static void addSalaryRow(PdfPTable table, String label, String value) {
        table.addCell(new Phrase(label, HIGHLIGHT_FONT));
        table.addCell(new Phrase(value, NORMAL_FONT));
    }

    private static void addProfileRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HIGHLIGHT_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    private static String formatCurrency(double amount) {
        return String.format("%,.2f €", amount);
    }

    public static void exportEmployeeDetails(Employe employe, String absolutePath)
            throws DocumentException, IOException {

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(absolutePath));

        document.open();
        addDocumentHeader(writer, document, "Détails Employé - " + employe.getFullName());
        addEmployeeDetailsContent(document, employe);
        addDocumentFooter(writer, document);
        document.close();
    }

    private static void addEmployeeDetailsContent(Document document, Employe employe)
            throws DocumentException {

        // Personal Information Section
        Paragraph sectionHeader = new Paragraph("Informations Personnelles", HIGHLIGHT_FONT);
        sectionHeader.setSpacingAfter(10f);
        document.add(sectionHeader);

        PdfPTable personalTable = new PdfPTable(2);
        personalTable.setWidthPercentage(80);
        personalTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        addDetailRow(personalTable, "ID Employé:", String.valueOf(employe.getIdEmploye()));
        addDetailRow(personalTable, "Nom Complet:", employe.getFullName());
        addDetailRow(personalTable, "Date de Naissance:",
                employe.getDateNaissance() != null ? employe.getDateNaissance().format(DATE_FORMATTER) : "N/A");
        addDetailRow(personalTable, "CIN:", employe.getCin());
        addDetailRow(personalTable, "CNSS:", employe.getCnss());
        addDetailRow(personalTable, "Téléphone:", employe.getTelephone());
        addDetailRow(personalTable, "Email:", employe.getEmail());
        addDetailRow(personalTable, "Adresse:", employe.getAdresse());

        document.add(personalTable);

        // Professional Information Section
        sectionHeader = new Paragraph("Informations Professionnelles", HIGHLIGHT_FONT);
        sectionHeader.setSpacingBefore(15f);
        sectionHeader.setSpacingAfter(10f);
        document.add(sectionHeader);

        PdfPTable professionalTable = new PdfPTable(2);
        professionalTable.setWidthPercentage(80);
        professionalTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        addDetailRow(professionalTable, "Poste:", employe.getPoste());
        addDetailRow(professionalTable, "Service:", employe.getServiceName());
        addDetailRow(professionalTable, "Date d'Embauche:", employe.getDateEmbauche().format(DATE_FORMATTER));
        addDetailRow(professionalTable, "Ancienneté:", employe.getAnciennete() + " années");
        addDetailRow(professionalTable, "Salaire de Base:", formatCurrency(employe.getSalaireDeBase()));
        addDetailRow(professionalTable, "Statut:", employe.getStatut());
        addDetailRow(professionalTable, "Type de Contrat:", employe.getTypeContrat());

        document.add(professionalTable);

        // Add photo placeholder if available
        if (employe.getPhotoPath() != null && !employe.getPhotoPath().isEmpty()) {
            try {
                Image photo = Image.getInstance(employe.getPhotoPath());
                photo.scaleToFit(100, 100);
                photo.setAlignment(Image.ALIGN_RIGHT);
                document.add(photo);
            } catch (Exception e) {
                // Silently ignore photo errors
            }
        }
    }

    private static void addDetailRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HIGHLIGHT_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(5f);
        table.addCell(valueCell);
    }
}