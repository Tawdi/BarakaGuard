package main.java.com.barakaguard.service;

import main.java.com.barakaguard.dao.TransactionDAO;
import main.java.com.barakaguard.dao.implementation.TransactionDAOImpl;
import main.java.com.barakaguard.dto.transaction.TransactionFilter;
import main.java.com.barakaguard.entity.compte.Compte;
import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.entity.transaction.TypeTransaction;
import main.java.com.barakaguard.exception.DAOException;
import main.java.com.barakaguard.exception.DatabaseException;
import main.java.com.barakaguard.service.interfaces.ICompteService;
import main.java.com.barakaguard.service.interfaces.ITransactionService;
import main.java.com.barakaguard.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionService implements ITransactionService {

    private final TransactionDAO transactionDAO;
    private final ICompteService compteService = new CompteService();

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

    public Map<TypeTransaction, List<Transaction>> groupByType() {
        return getAll().stream()
                .collect(Collectors.groupingBy(Transaction::type));
    }

    public Map<Integer, List<Transaction>> groupByYear() {
        return getAll().stream()
                .collect(Collectors.groupingBy(t -> t.date().getYear()));
    }

    public Map<String, List<Transaction>> groupByMonth() {
        return getAll().stream()
                .collect(Collectors.groupingBy(t -> t.date().getYear() + "-" + t.date().getMonthValue()));
    }

    public List<Transaction> getAll(TransactionFilter filter) {
        return getAll().stream()
                .filter(t -> filter.getMinMontant() == null || Math.abs(t.montant()) >= filter.getMinMontant())
                .filter(t -> filter.getMaxMontant() == null || Math.abs(t.montant()) <= filter.getMaxMontant())
                .filter(t -> filter.getType() == null || t.type().equals(filter.getType()))
                .filter(t -> filter.getLieu() == null ||  t.lieu().toLowerCase().contains(filter.getLieu().toLowerCase()))
                .filter(t -> filter.getStartDate() == null || !t.date().isBefore(filter.getStartDate()))
                .filter(t -> filter.getEndDate() == null || !t.date().isAfter(filter.getEndDate()))
                .toList();
    }

    public List<Transaction> getByCompteId(UUID id) {
        try {
            return transactionDAO.findByCompteId(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération des transactions : " + e.getMessage());
            return List.of();
        }
    }

    public Map<UUID, List<Transaction>> getByClientId(UUID id) {
        try {
            return transactionDAO.findByClientId(id);
        } catch (DAOException e) {
            System.err.println("Erreur lors de la récupération des transactions : " + e.getMessage());
            return Map.of();
        }
    }

    public void retrait(UUID compteId, double montant, String lieu) {
        try {
            Compte compte = compteService.getById(compteId)
                    .orElseThrow(() -> new IllegalArgumentException("Compte introuvable"));

            if (compte.getSolde() < montant) {
                throw new IllegalArgumentException("Solde insuffisant !");
            }

            compte.setSolde(compte.getSolde() - montant);
            compteService.update(compte);

            Transaction t = new Transaction(UUID.randomUUID(), LocalDateTime.now(), -montant, TypeTransaction.RETRAIT,
                    lieu, compteId);

            transactionDAO.save(t);

            System.out.println("✅ Retrait de " + montant + " effectué sur le compte " + compteId);

        } catch (DAOException e) {
            System.err.println("❌ Erreur lors du retrait : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Opération invalide : " + e.getMessage());
        }
    }

    public void versement(UUID compteId, double montant, String lieu) {
        try {
            Compte compte = compteService.getById(compteId)
                    .orElseThrow(() -> new IllegalArgumentException("Compte introuvable"));

            compte.setSolde(compte.getSolde() + montant);
            compteService.update(compte);

            Transaction t = new Transaction(UUID.randomUUID(), LocalDateTime.now(), montant, TypeTransaction.VERSEMENT,
                    lieu, compteId);

            transactionDAO.save(t);

            System.out.println("✅ Versement de " + montant + " effectué sur le compte " + compteId);

        } catch (DAOException e) {
            System.err.println("❌ Erreur lors du versement : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Opération invalide : " + e.getMessage());
        }
    }

    public void virement(UUID sourceId, UUID destId, double montant, String lieu) {
        try {
            Compte source = compteService.getById(sourceId)
                    .orElseThrow(() -> new IllegalArgumentException("Compte source introuvable"));
            Compte dest = compteService.getById(destId)
                    .orElseThrow(() -> new IllegalArgumentException("Compte destination introuvable"));

            if (source.getSolde() < montant) {
                throw new IllegalArgumentException("Solde insuffisant !");
            }

            source.setSolde(source.getSolde() - montant);
            dest.setSolde(dest.getSolde() + montant);

            compteService.update(source);
            compteService.update(dest);

            Transaction tSource = new Transaction(UUID.randomUUID(), LocalDateTime.now(), -montant,
                    TypeTransaction.VIREMENT, lieu, sourceId);

            Transaction tDest = new Transaction(UUID.randomUUID(), LocalDateTime.now(), montant,
                    TypeTransaction.VIREMENT, lieu, destId);

            transactionDAO.save(tSource);
            transactionDAO.save(tDest);

            System.out.println("✅ Virement de " + montant + " effectué de " + sourceId + " vers " + destId);

        } catch (DAOException e) {
            System.err.println("❌ Erreur lors du virement : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Opération invalide : " + e.getMessage());
        }
    }

}
