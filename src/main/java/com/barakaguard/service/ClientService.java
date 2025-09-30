package main.java.com.barakaguard.service;

import main.java.com.barakaguard.dao.ClientDAO;
import main.java.com.barakaguard.dao.implementation.ClientDAOImpl;
import main.java.com.barakaguard.exception.DAOException;
import main.java.com.barakaguard.exception.DatabaseException;
import main.java.com.barakaguard.entity.client.Client;
import main.java.com.barakaguard.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientService {

    private final ClientDAO clientDAO;

    public ClientService() {
        try {
            Connection connection = Database.getConnection();
            this.clientDAO = new ClientDAOImpl(connection);
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de se connecter à la base de données", e);
        }
    }

    public void add(Client client) {
        try {
            clientDAO.save(client);
        } catch (DAOException e) {
            System.err.println("Erreur lors de l'ajout du client : " + e.getMessage());
        }
    }

    public void update(Client client) {
        try {
            clientDAO.update(client);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la mise à jour du client : " + e.getMessage());
        }
    }

    public void delete(UUID id) {
        try {
            clientDAO.delete(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la suppression du client : " + e.getMessage());
        }
    }

    public Optional<Client> getById(UUID id) {
        try {
            return clientDAO.findById(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la recherche du client : " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Client> getAll() {
        try {
            return clientDAO.findAll();
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération des clients : " + e.getMessage());
            return List.of();
        }
    }

}
