package com.employeemanagement.dao;

import com.employeemanagement.config.DatabaseConnection;
import com.employeemanagement.models.Salaire;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for salary management
 */
public class SalaireDAO {
    // SQL queries
    private static final String FIND_ALL_SQL =
            "SELECT s.*, CONCAT(e.prenom, ' ', e.nom) as employe_nom " +
                    "FROM salaire s " +
                    "JOIN employe e ON s.idEmploye = e.idEmploye " +
                    "ORDER BY s.annee DESC, s.mois DESC";

    private static final String FIND_BY_EMPLOYEE_SQL =
            "SELECT s.*, CONCAT(e.prenom, ' ', e.nom) as employe_nom " +
                    "FROM salaire s " +
                    "JOIN employe e ON s.idEmploye = e.idEmploye " +
                    "WHERE s.idEmploye = ? " +
                    "ORDER BY s.annee DESC, s.mois DESC";

    private static final String FIND_BY_ID_SQL =
            "SELECT s.*, CONCAT(e.prenom, ' ', e.nom) as employe_nom " +
                    "FROM salaire s " +
                    "JOIN employe e ON s.idEmploye = e.idEmploye " +
                    "WHERE s.idSalaire = ?";

    private static final String UPSERT_SQL =
            "INSERT INTO salaire (idEmploye, mois, annee, salaireBrut, deductions, salaireNet, datePaiement) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "salaireBrut = VALUES(salaireBrut), " +
                    "deductions = VALUES(deductions), " +
                    "salaireNet = VALUES(salaireNet), " +
                    "datePaiement = VALUES(datePaiement)";

    private static final String UPDATE_SQL =
            "UPDATE salaire SET idEmploye = ?, mois = ?, annee = ?, " +
                    "salaireBrut = ?, deductions = ?, salaireNet = ?, datePaiement = ? " +
                    "WHERE idSalaire = ?";

    private static final String DELETE_SQL =
            "DELETE FROM salaire WHERE idSalaire = ?";



    /**
     * Retrieves all salary records
     */
    public List<Salaire> findAll() throws SQLException { // findAll method is used to find all salary records
        List<Salaire> salaires = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                salaires.add(mapToSalaire(rs)); // add the salary record to the list
            }
        }
        return salaires;
    }

    /**
     * Finds salaries for a specific employee
     */
    public List<Salaire> findByEmployeeId(int employeeId) throws SQLException { // findByEmployeeId method is used to find salaries for a specific employee
        List<Salaire> salaires = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMPLOYEE_SQL)) {

            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) { 
                while (rs.next()) {
                    salaires.add(mapToSalaire(rs)); // add the salary record to the list
                }
            }
        }
        return salaires;
    }

    /**
     * Finds a specific salary record by ID
     */
    public Optional<Salaire> findById(int id) throws SQLException { // findById method is used to find a specific salary record by ID
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) { // PreparedStatement is used to execute the findById statement

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToSalaire(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Saves a salary record (creates or updates)
     */
    public void save(Salaire salaire) throws SQLException { // save method is used to save a salary record
        System.out.println("SalaireDAO: save method called for Salaire ID: " + salaire.getIdSalaire());
        if (salaire.getIdSalaire() == 0) {
            insert(salaire);
        } else {
            update(salaire);
        }
    }

    private void insert(Salaire salaire) throws SQLException { // insert method is used to insert a salary record
        System.out.println("SalaireDAO: insert method called for Salaire: " + salaire);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPSERT_SQL, Statement.RETURN_GENERATED_KEYS)) { // PreparedStatement is used to execute the upsert statement

            setCommonParameters(stmt, salaire); // set the common parameters for the insert statement
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    salaire.setIdSalaire(generatedKeys.getInt(1)); // set the id of the salary record
                }
            }
        }
    }

    private void update(Salaire salaire) throws SQLException { // update method is used to update a salary record
        System.out.println("SalaireDAO: update method called for Salaire ID: " + salaire.getIdSalaire());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) { // PreparedStatement is used to execute the update statement

            setCommonParameters(stmt, salaire); // set the common parameters for the update statement
            stmt.setInt(8, salaire.getIdSalaire()); // set the id of the salary record
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a salary record
     */
    public void delete(int id) throws SQLException { // delete method is used to delete a salary record
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) { // PreparedStatement is used to execute the delete statement

            stmt.setInt(1, id); // set the id of the salary record
            stmt.executeUpdate();
        }
    }

    private void setCommonParameters(PreparedStatement stmt, Salaire salaire)
            throws SQLException { // setCommonParameters method is used to set the common parameters for the insert and update statements
        stmt.setInt(1, salaire.getEmployeId());
        stmt.setInt(2, salaire.getMois());
        stmt.setInt(3, salaire.getAnnee());
        stmt.setDouble(4, salaire.getSalaireBrut());
        stmt.setDouble(5, salaire.getDeductions());
        stmt.setDouble(6, salaire.getSalaireNet());
        stmt.setDate(7, java.sql.Date.valueOf(salaire.getDatePaiement()));
    }

    private Salaire mapToSalaire(ResultSet rs) throws SQLException { // mapToSalaire method is used to map the result set to the salary record object
        Salaire salaire = new Salaire();
        salaire.setIdSalaire(rs.getInt("idSalaire"));
        salaire.setEmployeId(rs.getInt("idEmploye"));
        salaire.setEmployeNom(rs.getString("employe_nom"));
        salaire.setMois(rs.getInt("mois"));
        salaire.setAnnee(rs.getInt("annee"));
        salaire.setSalaireBrut(rs.getDouble("salaireBrut"));
        salaire.setDeductions(rs.getDouble("deductions"));
        salaire.setSalaireNet(rs.getDouble("salaireNet"));
        return salaire;
    }
}