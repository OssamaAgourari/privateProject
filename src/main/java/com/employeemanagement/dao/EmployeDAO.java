package com.employeemanagement.dao;

import com.employeemanagement.config.DatabaseConnection;
import com.employeemanagement.models.Employe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeDAO {
    // SQL queries using your existing French column names          // Quand quelque chose est final, ça veut dire qu'on ne peut plus le changer après l'avoir défini.
    private static final String FIND_ALL_SQL =                      // Quand quelque chose est static, il n'est pas lié à un objet, mais à la classe elle-même.
            "SELECT e.*, s.nom as service_nom FROM employe e " +
                    "LEFT JOIN service s ON e.idService = s.idService " +
                    "WHERE e.statut = 'ACTIF' " +
                    "ORDER BY e.nom, e.prenom";

    private static final String FIND_ALL_ACTIVE_SQL =
            "SELECT e.*, s.nom as service_nom FROM employe e " +
                    "LEFT JOIN service s ON e.idService = s.idService " +
                    "WHERE e.statut = 'ACTIF' " +
                    "ORDER BY e.nom, e.prenom";

    private static final String INSERT_SQL =
            "INSERT INTO employe (nom, prenom, poste, idService, dateEmbauche, salaireDeBase, cin, cnss, telephone, email, adresse, statut, typeDeContrat, actif) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE employe SET nom = ?, prenom = ?, poste = ?, idService = ?, " +
                    "dateEmbauche = ?, salaireDeBase = ?, cin = ?, cnss = ?, telephone = ?, email = ?, adresse = ?, statut = ?, typeDeContrat = ?, actif = ? WHERE idEmploye = ?";

    private static final String SOFT_DELETE_SQL =
            "UPDATE employe SET statut = 'INACTIF' WHERE idEmploye = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT e.*, s.nom as service_nom FROM employe e " +
                    "LEFT JOIN service s ON e.idService = s.idService " +
                    "WHERE e.idEmploye = ?";

    public List<Employe> findAll() throws SQLException { // findAll method is used to find all employees
        List<Employe> employes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL); // PreparedStatement is used to execute the findAll statement
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employes.add(mapToEmploye(rs)); // add the employe object to the list
            }
        }
        return employes;
    }

    public List<Employe> getAllActiveEmployes() throws SQLException { // getAllActiveEmployes method is used to find all active employees
        List<Employe> employes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_ACTIVE_SQL); // PreparedStatement is used to execute the findAllActive statement
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employes.add(mapToEmploye(rs)); // add the employe object to the list
            }
        }
        return employes;
    }

    public Optional<Employe> findById(int id) throws SQLException { // findById method is used to find an employee by id
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) { // PreparedStatement is used to execute the findById statement

            stmt.setInt(1, id); // set the id parameter for the findById statement

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEmploye(rs)); // return the employe object if it exists
                }
            }
        }
        return Optional.empty(); // return an empty optional if the employe object does not exist
    }

    public void save(Employe employe) throws SQLException { // save method is used to save an employee
        if (employe.getIdEmploye() == 0) {
            insert(employe);
        } else {
            update(employe);
        }
    }

    private void insert(Employe employe) throws SQLException { // insert method is used to insert an employee
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) { // PreparedStatement is used to execute the insert statement

            setCommonParameters(stmt, employe); // set the common parameters for the insert statement
            stmt.setBoolean(14, employe.isActif()); // Set actif status at index 14
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employe.setIdEmploye(generatedKeys.getInt(1)); // set the id parameter for the insert statement
                }
            }
        }
    }

    private void update(Employe employe) throws SQLException { // update method is used to update an employee
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) { // PreparedStatement is used to execute the update statement

            setCommonParameters(stmt, employe); // set the common parameters for the update statement
            stmt.setBoolean(14, employe.isActif()); // Set actif status at index 14
            stmt.setInt(15, employe.getIdEmploye()); // set the id parameter for the update statement at index 15
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException { // delete method is used to delete an employee
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SOFT_DELETE_SQL)) { // PreparedStatement is used to execute the delete statement

            stmt.setInt(1, id); // setInt method is used to set the id parameter for the delete statement
            stmt.executeUpdate();
        }
    }

    private void setCommonParameters(PreparedStatement stmt, Employe employe) throws SQLException { // setCommonParameters method is used to set the common parameters for the insert and update statements
        stmt.setString(1, employe.getNom());
        stmt.setString(2, employe.getPrenom());
        stmt.setString(3, employe.getPoste());
        stmt.setInt(4, employe.getServiceId());
        stmt.setDate(5, Date.valueOf(employe.getDateEmbauche()));
        stmt.setDouble(6, employe.getSalaireDeBase());
        stmt.setString(7, employe.getCin());
        stmt.setString(8, employe.getCnss());
        stmt.setString(9, employe.getTelephone());
        stmt.setString(10, employe.getEmail());
        stmt.setString(11, employe.getAdresse());
        stmt.setString(12, employe.getStatut());
        stmt.setString(13, employe.getTypeContrat());
        // Note: actif is handled separately in insert/update methods as it's not always in the common set
    }

    private Employe mapToEmploye(ResultSet rs) throws SQLException { // mapToEmploye method is used to map the result set to the employe object
        Employe employe = new Employe();
        employe.setIdEmploye(rs.getInt("idEmploye"));
        employe.setNom(rs.getString("nom"));
        employe.setPrenom(rs.getString("prenom"));
        employe.setPoste(rs.getString("poste"));
        employe.setServiceId(rs.getInt("idService")); // Corrected column name to idService
        employe.setServiceName(rs.getString("service_nom"));
        employe.setDateEmbauche(rs.getDate("dateEmbauche").toLocalDate());
        employe.setSalaireDeBase(rs.getDouble("salaireDeBase"));
        employe.setActif(rs.getBoolean("actif")); // Retrieve boolean actif status
        employe.setCin(rs.getString("cin"));
        employe.setCnss(rs.getString("cnss"));
        employe.setTelephone(rs.getString("telephone"));
        employe.setEmail(rs.getString("email"));
        employe.setAdresse(rs.getString("adresse"));
        employe.setStatut(rs.getString("statut"));
        employe.setTypeContrat(rs.getString("typeDeContrat"));
        return employe;
    }
}