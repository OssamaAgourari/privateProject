package com.employeemanagement.utils;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utility class for date and time conversions between java.util.Date, java.sql.Date,
 * java.sql.Time, and java.time classes
 */
public class DateUtils {

    /**
     * Converts LocalDate to java.util.Date
     */
    public static Date toDate(LocalDate localDate) {
        return localDate == null ? null : java.sql.Date.valueOf(localDate);
    }

    /**
     * Converts java.util.Date to LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return date == null ? null : new java.sql.Date(date.getTime()).toLocalDate();
    }

    /**
     * Converts LocalTime to java.util.Date (actually stores as java.sql.Time)
     */
    public static Date toTime(LocalTime localTime) {
        return localTime == null ? null : Time.valueOf(localTime);
    }

    /**
     * Converts java.util.Date (containing time) to LocalTime
     */
    public static LocalTime toLocalTime(Date date) {
        return date == null ? null : new Time(date.getTime()).toLocalTime();
    }

    /**
     * Formats LocalDate to French date string (dd/MM/yyyy)
     */
    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Formats LocalTime to French time string (HH:mm)
     */
    public static String formatTime(LocalTime time) {
        return time == null ? "" : time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Checks if a date is today
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * Gets current time truncated to minutes
     */
    public static LocalTime currentTime() {
        return LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    }
}

/** 
 *🧭 Pourquoi l’utiliser ?
 *Java a plusieurs types de dates, comme :
 *java.util.Date (ancien, utilisé dans les bibliothèques)
 *java.sql.Date / java.sql.Time (pour les bases de données)
 *java.time.LocalDate / LocalTime (plus moderne, depuis Java 8)
 
 *DateUtils permet de convertir facilement entre ces formats et d’effectuer des actions fréquentes.
 */