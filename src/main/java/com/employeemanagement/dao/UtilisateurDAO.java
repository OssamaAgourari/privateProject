package com.employeemanagement.dao;

import com.employeemanagement.config.DatabaseConnection;
import com.employeemanagement.models.Utilisateur;
import com.employeemanagement.utils.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for user authentication and management
 */
public class UtilisateurDAO {
    // SQL queries
    private static final String AUTHENTICATE_SQL =
            "SELECT * FROM utilisateur WHERE nomUtilisateur = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM utilisateur WHERE idUtilisateur = ?";

    private static final String INSERT_SQL =
            "INSERT INTO utilisateur (nomUtilisateur, motDePasse, role, idEmploye) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE utilisateur SET nomUtilisateur = ?, role = ?, idEmploye = ? " +
                    "WHERE idUtilisateur = ?";

    private static final String UPDATE_PASSWORD_SQL =
            "UPDATE utilisateur SET motDePasse = ? " +
                    "WHERE idUtilisateur = ?";

    private static final String DELETE_SQL =
            "DELETE FROM utilisateur WHERE idUtilisateur = ?";

    private static final String FIND_ALL = 
            "SELECT u.*, e.nom as employeNom FROM utilisateur u " +
            "LEFT JOIN employe e ON u.idEmploye = e.idEmploye";

    /**
     * Authenticates a user with password hashing verification
     */
    public Optional<Utilisateur> authenticate(String username, String password) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTHENTICATE_SQL)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("motDePasse");
                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        Utilisateur utilisateur = new Utilisateur();
                        utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                        utilisateur.setNomUtilisateur(rs.getString("nomUtilisateur"));
                        utilisateur.setMotDePasse(rs.getString("motDePasse"));
                        utilisateur.setRole(rs.getString("role"));
                        utilisateur.setIdEmploye(rs.getInt("idEmploye"));
                        return Optional.of(utilisateur);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds user by ID
     */
    public Optional<Utilisateur> findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUtilisateur(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a new user with hashed password
     */
    public void create(Utilisateur user) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getNomUtilisateur()); // place le nom de l’utilisateur en 1er paramètre de la requête SQL.
            stmt.setString(2, user.getMotDePasse()); // place le mot de passe de l’utilisateur en 2ème paramètre de la requête SQL.
            stmt.setString(3, user.getRole()); // place le rôle de l’utilisateur en 3ème paramètre de la requête SQL.
            stmt.setInt(4, user.getIdEmploye()); // place l’id de l’employé en 4ème paramètre de la requête SQL.
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setIdUtilisateur(generatedKeys.getInt(1)); // place l’id de l’utilisateur en 1er paramètre de la requête SQL.
                }
            }
        }
    }

    /**
     * Updates user information (excluding password)
     */
    public void update(Utilisateur user) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, user.getNomUtilisateur());
            stmt.setString(2, user.getRole());
            stmt.setInt(3, user.getIdEmploye());
            stmt.setInt(4, user.getIdUtilisateur());
            stmt.executeUpdate();
        }
    }

    /**
     * Updates user password with hashing
     */
    public void updatePassword(int userId, String newPassword) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PASSWORD_SQL)) {

            stmt.setString(1, PasswordUtil.hashPassword(newPassword));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a user
     */
    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id); // place l’id de l’utilisateur en 1er paramètre de la requête SQL.
            stmt.executeUpdate();
        }
    }

    /**
     * Saves or updates a user record
     */
    public void save(Utilisateur utilisateur) throws SQLException {
        // Validate the role before saving
        String role = utilisateur.getRole();
        List<String> validRoles = Arrays.asList("ADMIN", "RH", "MANAGER", "EMPLOYEE");
        if (!validRoles.contains(role)) {
            throw new SQLException("Rôle invalide. Rôles valides: " + validRoles);
        }

        if (utilisateur.getIdUtilisateur() == 0) {
            create(utilisateur);
        } else {
            update(utilisateur);
        }
    }

    public List<Utilisateur> findAll() throws SQLException { // findAll method is used to find all users
        List<Utilisateur> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL)) {
            
            while (rs.next()) {
                Utilisateur user = mapToUtilisateur(rs);
                user.setEmployeNom(rs.getString("employeNom")); // place le nom de l’employé en 1er paramètre de la requête SQL.
                users.add(user);
            }
        }
        return users;
    }

    private Utilisateur mapToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setIdUtilisateur(rs.getInt("idUtilisateur"));
        user.setNomUtilisateur(rs.getString("nomUtilisateur"));
        user.setMotDePasse(rs.getString("motDePasse"));
        user.setRole(rs.getString("role"));
        // Check if the column exists before reading to avoid error if query changes
        try {
            rs.findColumn("idEmploye");
            user.setIdEmploye(rs.getInt("idEmploye"));
        } catch (SQLException e) {
            // Column not found, set to default or handle as needed
            user.setIdEmploye(0); // Assuming 0 is not a valid employee ID
        }

        // Check if the column exists before reading to avoid error if query changes
        try {
            rs.findColumn("employeNom");
            user.setEmployeNom(rs.getString("employeNom"));
        } catch (SQLException e) {
            // Column not found, set to empty string or handle as needed
            user.setEmployeNom("N/A");
        }
        return user;
    }
}