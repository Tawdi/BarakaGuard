package main.java.com.barakaguard.dao.implementation;

import main.java.com.barakaguard.dao.ClientDAO;
import main.java.com.barakaguard.exception.DAOException;
import main.java.com.barakaguard.entity.client.Client;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ClientDAOImpl implements ClientDAO {

    private final Connection connection;

    public ClientDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Client client) {
        String sql = "INSERT INTO Client (id, nom, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, client.id());
            stmt.setString(2, client.nom());
            stmt.setString(3, client.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de l'insertion du client", e);
        }
    }

    @Override
    public Optional<Client> findById(UUID id) {
        String sql = "SELECT * FROM Client WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            return Optional.ofNullable(rs.next() ? mapToObject(rs) : null);
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du client par ID", e);
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT * FROM Client";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            List<Client> clients = new ArrayList<>();
            while (rs.next()) {
                clients.add(mapToObject(rs));
            }

            return clients.stream()
                    .sorted(Comparator.comparing(Client::nom))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de tous les clients", e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = "UPDATE Client SET nom = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, client.nom());
            stmt.setString(2, client.email());
            stmt.setObject(3, client.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du client", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM Client WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du client", e);
        }
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        String sql = "SELECT * FROM Client WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return Optional.ofNullable(rs.next() ? mapToObject(rs) : null);
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du client par email", e);
        }
    }


    private Client mapToObject(ResultSet rs) throws SQLException {
        return new Client(
                (UUID) rs.getObject("id"),
                rs.getString("nom"),
                rs.getString("email"));
    }
}
