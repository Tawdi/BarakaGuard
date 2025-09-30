package main.java.com.barakaguard.service;

import main.java.com.barakaguard.dao.CompteDAO;
import main.java.com.barakaguard.dao.implementation.CompteDAOImpl;
import main.java.com.barakaguard.entity.compte.Compte;
import main.java.com.barakaguard.exception.DAOException;
import main.java.com.barakaguard.exception.DatabaseException;
import main.java.com.barakaguard.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CompteService {

    private final CompteDAO compteDAO;

    public CompteService() {
        try {
            Connection connection = Database.getConnection();
            this.compteDAO = new CompteDAOImpl(connection);
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de se connecter à la base de données", e);
        }
    }

    public void add(Compte compte) {
        try {
            compteDAO.save(compte);
        } catch (DAOException e) {
            System.err.println("Erreur lors de l'ajout du compte : " + e.getMessage());
        }
    }

    public void update(Compte compte) {
        try {
            compteDAO.update(compte);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la mise à jour du compte : " + e.getMessage());
        }
    }

    public void delete(UUID id) {
        try {
            compteDAO.delete(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la suppression du compte : " + e.getMessage());
        }
    }

    public Optional<Compte> getById(UUID id) {
        try {
            return compteDAO.findById(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération du compte : " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Compte> getAll() {
        try {
            return compteDAO.findAll();
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération de tous les comptes : " + e.getMessage());
            return List.of();
        }
    }

    public List<Compte> getByClientId(UUID clientId) {
        try {
            return compteDAO.findByClientId(clientId);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération des comptes du client : " + e.getMessage());
            return List.of();
        }
    }

    public Optional<Compte> getByNumero(String numero) {
        try {
            return compteDAO.findByNumero(numero);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la recherche du compte par numéro : " + e.getMessage());
            return Optional.empty();
        }
    }
}
