package com.employeemanagement.utils;

import com.employeemanagement.models.Salaire;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Utility class for salary calculations and validations
 */
public class SalaryCalculator {
    // Constants for salary calculations
    private static final double CNSS_RATE = 0.07; // 7% CNSS contribution
    private static final double AMO_RATE = 0.02; // 2% AMO contribution
    private static final double TAX_RATE = 0.15; // 15% income tax
    private static final double OVERTIME_RATE = 1.25; // 25% extra for overtime hours

    /**
     * Calculates the total salary with all components
     * @param salaire The salary object to calculate
     * @return The calculated salary object with all components
     */
    public static Salaire calculateSalary(Salaire salaire) {
        // Calculate overtime pay
        double overtimePay = salaire.getHeuresSupplementaires() * 
                           salaire.getTauxHeuresSupplementaires() * 
                           OVERTIME_RATE;

        // Calculate base gross salary
        double baseGross = salaire.getMontant() + overtimePay + 
                          salaire.getPrimes() + salaire.getAvantages();

        // Calculate deductions
        double cnssDeduction = baseGross * CNSS_RATE;
        double amoDeduction = baseGross * AMO_RATE;
        double taxableIncome = baseGross - cnssDeduction - amoDeduction;
        double taxDeduction = taxableIncome * TAX_RATE;

        // Set all components
        salaire.setCotisations(cnssDeduction + amoDeduction);
        salaire.setRetenues(taxDeduction);

        return salaire;
    }

    /**
     * Validates if a salary record is for a valid period
     * @param mois Month (1-12)
     * @param annee Year
     * @return true if the period is valid
     */
    public static boolean isValidPeriod(int mois, int annee) {
        if (mois < 1 || mois > 12) {
            return false;
        }

        int currentYear = YearMonth.now().getYear();
        if (annee < 2000 || annee > currentYear + 1) {
            return false;
        }

        // Can't create salary records for future months
        YearMonth current = YearMonth.now();
        YearMonth salaryPeriod = YearMonth.of(annee, mois);
        return !salaryPeriod.isAfter(current);
    }

    /**
     * Calculates the number of working days in a month
     * @param mois Month (1-12)
     * @param annee Year
     * @return Number of working days (excluding weekends)
     */
    public static int getWorkingDaysInMonth(int mois, int annee) {
        YearMonth yearMonth = YearMonth.of(annee, mois);
        int daysInMonth = yearMonth.lengthOfMonth();
        int workingDays = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            // Check if it's not a weekend (Saturday = 6, Sunday = 7)
            if (date.getDayOfWeek().getValue() < 6) {
                workingDays++;
            }
        }

        return workingDays;
    }

    /**
     * Formats a salary amount with currency symbol
     * @param amount The amount to format
     * @return Formatted string with currency symbol
     */
    public static String formatSalary(double amount) {
        return String.format("%,.2f MAD", amount);
    }
} 