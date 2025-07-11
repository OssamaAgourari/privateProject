package com.employeemanagement.dao;

import com.employeemanagement.config.DatabaseConnection;
import com.employeemanagement.models.Pointage;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for time tracking records (pointage)
 */
public class PointageDAO {
    private static final String FIND_ALL_SQL =
            "SELECT p.*, CONCAT(e.prenom, ' ', e.nom) as employe_nom " +
                    "FROM pointage p " +
                    "JOIN employe e ON p.idEmploye = e.idEmploye " +
                    "ORDER BY p.date DESC";

    private static final String FIND_BY_EMPLOYEE_SQL =
            "SELECT p.* FROM pointage p " +
                    "WHERE p.idEmploye = ? AND p.date = ?";

    private static final String FIND_ALL_BY_EMPLOYEE_SQL = // FIND_ALL_BY_EMPLOYEE_SQL is used to find all pointages for a specific employee
            "SELECT p.*, CONCAT(e.prenom, ' ', e.nom) as employe_nom " +
                    "FROM pointage p " +
                    "JOIN employe e ON p.idEmploye = e.idEmploye " +
                    "WHERE p.idEmploye = ? " +
                    "ORDER BY p.date DESC";

    private static final String UPSERT_SQL =
            "INSERT INTO pointage (idEmploye, date, heureArrivee, heureDepart) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "heureArrivee = VALUES(heureArrivee), " +
                    "heureDepart = VALUES(heureDepart)";

    private static final String FIND_BY_ID_SQL =
            "SELECT p.* FROM pointage p WHERE p.idPointage = ?";

    private static final String DELETE_SQL =
            "DELETE FROM pointage WHERE idPointage = ?";

    private static final String INSERT_SQL =
            "INSERT INTO pointage (idEmploye, date, heureArrivee, heureDepart) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE pointage SET idEmploye = ?, date = ?, heureArrivee = ?, " +
                    "heureDepart = ? WHERE idPointage = ?";

    /**
     * Saves or updates a pointage record
     */
    public void savePointage(Pointage pointage) throws SQLException {
        if (pointage == null) {
            throw new IllegalArgumentException("Pointage cannot be null");
        }

        // Calculate duration before saving
        pointage.calculateDuree();

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (pointage.getId() == 0) {
                // Insert new record
                try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                    setStatementParameters(stmt, pointage); // Set parameters for the insert statement
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) { // if the result set has a next value
                            pointage.setId(rs.getInt(1)); // set the id of the pointage
                        }
                    }
                }
            } else {
                // Update existing record
                try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                    setStatementParameters(stmt, pointage);
                    stmt.setInt(5, pointage.getId()); // set the id of the pointage
                    stmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Helper method to set prepared statement parameters
     */
    private void setStatementParameters(PreparedStatement stmt, Pointage pointage)
            throws SQLException {
        stmt.setInt(1, pointage.getEmployeId());
        stmt.setDate(2, Date.valueOf(pointage.getDate()));

        if (pointage.getHeureArrivee() != null) {
            stmt.setTime(3, Time.valueOf(pointage.getHeureArrivee()));
        } else {
            stmt.setNull(3, Types.TIME);
        }

        if (pointage.getHeureDepart() != null) {
            stmt.setTime(4, Time.valueOf(pointage.getHeureDepart()));
        } else {
            stmt.setNull(4, Types.TIME);
        }
    }

    public List<Pointage> findAll() throws SQLException { // findAll method is used to find all pointages
        List<Pointage> pointages = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pointages.add(mapToPointage(rs));
            }
        }
        return pointages;
    }

    public Optional<Pointage> findById(int id) throws SQLException { // findById method is used to find a pointage by its id
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToPointage(rs)); // return the pointage object if it exists
                }
            }
        }
        return Optional.empty(); // return an empty optional if the pointage object does not exist
    }

    public Optional<Pointage> findByEmployeeAndDate(int employeeId, LocalDate date) throws SQLException { // findByEmployeeAndDate method is used to find a pointage for a specific employee and date
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMPLOYEE_SQL)) {

            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToPointage(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Pointage> findByEmployeeId(int employeeId) throws SQLException { // findByEmployeeId method is used to find all pointages for a specific employee
        List<Pointage> pointages = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_BY_EMPLOYEE_SQL)) {

            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pointages.add(mapToPointage(rs));
                }
            }
        }
        return pointages;
    }

    public void save(Pointage pointage) throws SQLException { // save method is used to save a pointage
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPSERT_SQL)) { // PreparedStatement is used to execute the upsert statement

            stmt.setInt(1, pointage.getEmployeId());
            stmt.setDate(2, Date.valueOf(pointage.getDate()));
            stmt.setTime(3, pointage.getHeureArrivee() != null ?
                    Time.valueOf(pointage.getHeureArrivee()) : null);
            stmt.setTime(4, pointage.getHeureDepart() != null ?
                    Time.valueOf(pointage.getHeureDepart()) : null);

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException { // delete method is used to delete a pointage
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) { // PreparedStatement is used to execute the delete statement

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Pointage mapToPointage(ResultSet rs) throws SQLException {
        Pointage pointage = new Pointage();
        pointage.setIdPointage(rs.getInt("idPointage"));
        pointage.setEmployeId(rs.getInt("idEmploye"));

        try {
            pointage.setEmployeNom(rs.getString("employe_nom"));
        } catch (SQLException e) {
            // Column not present in all queries
        }

        pointage.setDate(rs.getDate("date").toLocalDate());

        Time arrivee = rs.getTime("heureArrivee");
        if (arrivee != null) {
            pointage.setHeureArrivee(arrivee.toLocalTime());
        }

        Time depart = rs.getTime("heureDepart");
        if (depart != null) {
            pointage.setHeureDepart(depart.toLocalTime());
        }

        return pointage;
    }
}