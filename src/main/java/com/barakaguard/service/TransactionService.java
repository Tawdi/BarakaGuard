package main.java.com.barakaguard.service;

import main.java.com.barakaguard.dao.TransactionDAO;
import main.java.com.barakaguard.dao.implementation.TransactionDAOImpl;
import main.java.com.barakaguard.dto.transaction.TransactionFilter;
import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.entity.transaction.TypeTransaction;
import main.java.com.barakaguard.exception.DAOException;
import main.java.com.barakaguard.exception.DatabaseException;
import main.java.com.barakaguard.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionService {

    private final TransactionDAO transactionDAO;

    public TransactionService() {
        try {
            Connection connection = Database.getConnection();
            this.transactionDAO = new TransactionDAOImpl(connection);
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de se connecter à la base de données", e);
        }
    }

    public void add(Transaction transaction) {
        try {
            transactionDAO.save(transaction);
        } catch (DAOException e) {
            System.err.println("Erreur lors de l'ajout de la transaction : " + e.getMessage());
        }
    }

    public void update(Transaction transaction) {
        try {
            transactionDAO.update(transaction);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la mise à jour de la transaction : " + e.getMessage());
        }
    }

    public void delete(UUID id) {
        try {
            transactionDAO.delete(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la suppression de la transaction : " + e.getMessage());
        }
    }

    public Optional<Transaction> getById(UUID id) {
        try {
            return transactionDAO.findById(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération de la transaction : " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Transaction> getAll() {
        try {
            return transactionDAO.findAll();
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération des transactions : " + e.getMessage());
            return List.of();
        }
    }

    public List<Transaction> getAll(TransactionFilter filter) {
        return getAll().stream()
                .filter(t -> filter.getMinMontant() == null || t.montant() >= filter.getMinMontant())
                .filter(t -> filter.getMaxMontant() == null || t.montant() <= filter.getMaxMontant())
                .filter(t -> filter.getType() == null || t.type().equals(filter.getType()))
                .filter(t -> filter.getLieu() == null || t.lieu().equalsIgnoreCase(filter.getLieu()))
                .filter(t -> filter.getStartDate() == null || !t.date().isBefore(filter.getStartDate()))
                .filter(t -> filter.getEndDate() == null || !t.date().isAfter(filter.getEndDate()))
                .toList();
    }

}
