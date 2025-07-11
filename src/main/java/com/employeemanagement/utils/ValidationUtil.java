package com.employeemanagement.utils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Comprehensive validation utility for form inputs and business rules
 */
public class ValidationUtil {
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+\\d{1,3}[- ]?)?\\d{10}$");
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-ZÀ-ÿ-' ]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    /**
     * Validates required fields with improved error handling
     */
    public static boolean validateRequiredFields(Component parent, JComponent[] fields, String[] fieldNames) { 
        // Validate that fields and fieldNames are not null and have the same length
        if (fields == null || fieldNames == null || fields.length != fieldNames.length) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        for (int i = 0; i < fields.length; i++) {
            String value = getComponentValue(fields[i]);
            if (value == null || value.trim().isEmpty()) {
                showValidationError(parent,
                        String.format("Le champ '%s' est obligatoire", fieldNames[i]));
                fields[i].requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    /**
     * Validates email format with detailed error message
     */
    public static boolean validateEmail(Component parent, JTextField emailField, String fieldName) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showValidationError(parent, String.format("%s est obligatoire", fieldName));
            emailField.requestFocusInWindow();
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showValidationError(parent, String.format("%s n'est pas valide", fieldName));
            emailField.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /**
     * Validates phone number format
     */
    public static boolean validatePhone(Component parent, JTextField phoneField, String fieldName) {
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            showValidationError(parent, String.format("%s n'est pas valide", fieldName));
            phoneField.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /**
     * Validates numeric input with range checking
     */
    public static boolean validateNumericRange(Component parent, JTextField numberField,
                                               String fieldName, double min, double max) {
        try {
            double value = Double.parseDouble(numberField.getText().trim());
            if (value < min || value > max) {
                showValidationError(parent,
                        String.format("%s doit être entre %s et %s", fieldName, min, max));
                numberField.requestFocusInWindow();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showValidationError(parent, String.format("%s doit être un nombre valide", fieldName));
            numberField.requestFocusInWindow();
            return false;
        }
    }

    /**
     * Validates date format and logical consistency
     */
    public static boolean validateDate(Component parent, JTextField dateField, String fieldName) {
        try {
            LocalDate.parse(dateField.getText().trim());
            return true;
        } catch (DateTimeParseException e) {
            showValidationError(parent,
                    String.format("%s doit être une date valide (format: AAAA-MM-JJ)", fieldName));
            dateField.requestFocusInWindow();
            return false;
        }
    }

    /**
     * Validates name fields (allows accented characters and hyphens)
     */
    public static boolean validateName(Component parent, JTextField nameField, String fieldName) {
        String name = nameField.getText().trim();
        if (!NAME_PATTERN.matcher(name).matches()) {
            showValidationError(parent,
                    String.format("%s contient des caractères invalides", fieldName));
            nameField.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /**
     * Validates password strength with configurable requirements
     */
    public static boolean validatePassword(Component parent, JPasswordField passwordField,
                                           String fieldName, int minLength, boolean requireSpecialChar) {
        char[] password = passwordField.getPassword();
        if (password.length < minLength) {
            showValidationError(parent,
                    String.format("%s doit contenir au moins %d caractères", fieldName, minLength));
            passwordField.requestFocusInWindow();
            return false;
        }

        if (requireSpecialChar && !new String(password).matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            showValidationError(parent,
                    String.format("%s doit contenir au moins un caractère spécial", fieldName));
            passwordField.requestFocusInWindow();
            return false;
        }

        return true;
    }

    /**
     * Shows consistent validation error messages
     */
    public static void showValidationError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                "Erreur de Validation", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Gets string value from various Swing components
     */
    private static String getComponentValue(JComponent component) {
        if (component instanceof JTextComponent) {
            return ((JTextComponent) component).getText().trim();
        } else if (component instanceof JComboBox) {
            Object selected = ((JComboBox<?>) component).getSelectedItem();
            return selected != null ? selected.toString().trim() : null;
        } else if (component instanceof JSpinner) {
            return ((JSpinner) component).getValue().toString().trim();
        }
        return null;
    }

    /**
     * Validates that end date is after start date
     */
    public static boolean validateDateRange(Component parent,
                                            LocalDate startDate, LocalDate endDate, String startFieldName, String endFieldName) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            showValidationError(parent,
                    String.format("%s doit être après %s", endFieldName, startFieldName));
            return false;
        }
        return true;
    }

    /**
     * Validates that a number is positive
     */
    public static boolean validatePositiveNumber(Component parent,
                                                 JTextField numberField, String fieldName) {
        try {
            double value = Double.parseDouble(numberField.getText().trim());
            if (value <= 0) {
                showValidationError(parent,
                        String.format("%s doit être un nombre positif", fieldName));
                numberField.requestFocusInWindow();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showValidationError(parent,
                    String.format("%s doit être un nombre valide", fieldName));
            numberField.requestFocusInWindow();
            return false;
        }
    }

    /**
     * Validates if a string represents a valid double number
     * @param numberStr the string to validate
     * @return true if valid double, false otherwise
     */
    public static boolean isValidDouble(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(numberStr.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}