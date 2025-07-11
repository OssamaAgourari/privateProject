package com.employeemanagement.dao;

import com.employeemanagement.config.DatabaseConnection;
import com.employeemanagement.models.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for department/service management
 */
public class ServiceDAO {
    // SQL queries
    private static final String FIND_ALL_SQL =
            "SELECT * FROM service ORDER BY nom";

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM service WHERE idService = ?";

    private static final String INSERT_SQL =
            "INSERT INTO service (nom, description) VALUES (?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE service SET nom = ?, description = ? WHERE idService = ?";

    private static final String DELETE_SQL =
            "DELETE FROM service WHERE idService = ?";

    /**
     * Retrieves all services/departments
     */
    public List<Service> findAll() throws SQLException {
        List<Service> services = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                services.add(mapToService(rs));
            }
        }
        return services;
    }

    /**
     * Finds a service by ID
     */
    public Optional<Service> findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToService(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Saves a service (creates or updates)
     */
    public void save(Service service) throws SQLException {
        if (service.getIdService() == 0) {
            insert(service);
        } else {
            update(service);
        }
    }

    private void insert(Service service) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, service.getNom());
            stmt.setString(2, service.getDescription());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    service.setIdService(generatedKeys.getInt(1));
                }
            }
        }
    }

    private void update(Service service) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, service.getNom());
            stmt.setString(2, service.getDescription());
            stmt.setInt(3, service.getIdService());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a service
     */
    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Service mapToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setIdService(rs.getInt("idService"));
        service.setNom(rs.getString("nom"));
        service.setDescription(rs.getString("description"));
        return service;
    }
}