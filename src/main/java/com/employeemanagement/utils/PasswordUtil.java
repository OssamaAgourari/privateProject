package com.employeemanagement.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt
 */
public class PasswordUtil {
    private static final int WORKLOAD = 12; // BCrypt workload factor

    /**
     * Hashes a plain text password using BCrypt
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORKLOAD));
    }

    /**
     * Verifies if a plain text password matches a hashed password
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to check against
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        System.out.println("PasswordUtil.verifyPassword - Plain password: " + plainPassword);
        System.out.println("PasswordUtil.verifyPassword - Hashed password from DB: " + hashedPassword);

        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        boolean isMatch = BCrypt.checkpw(plainPassword, hashedPassword);
        System.out.println("PasswordUtil.verifyPassword - BCrypt.checkpw result: " + isMatch);
        return isMatch;
    }

    /**
     * Validates password strength
     * @param password The password to validate
     * @return true if the password meets strength requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasNumber = true;
            else hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasNumber && hasSpecial;
    }
}