package main.java.com.barakaguard.dao.implementation;

import main.java.com.barakaguard.dao.TransactionDAO;
import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.entity.transaction.TypeTransaction;
import main.java.com.barakaguard.exception.DAOException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionDAOImpl implements TransactionDAO {
    private final Connection connection;

    public TransactionDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Transaction entity) {
        String sql = "INSERT INTO Transactions (id, date, montant, type, lieu, idCompte) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, entity.id());
            stmt.setTimestamp(2, Timestamp.valueOf(entity.date()));
            stmt.setDouble(3, entity.montant());
            stmt.setString(4, entity.type().name());
            stmt.setString(5, entity.lieu());
            stmt.setObject(6, entity.idCompte());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de l'insertion de la transaction", e);
        }
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        String sql = "SELECT * FROM Transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToObject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de la transaction", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM Transactions";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                transactions.add(mapToObject(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de toutes les transactions", e);
        }
        return transactions;
    }

    @Override
    public void update(Transaction entity) {
        String sql = "UPDATE Transactions SET date = ?, montant = ?, type = ?, lieu = ?, idCompte = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.date()));
            stmt.setDouble(2, entity.montant());
            stmt.setString(3, entity.type().name());
            stmt.setString(4, entity.lieu());
            stmt.setObject(5, entity.idCompte());
            stmt.setObject(6, entity.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de la transaction", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM Transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la transaction", e);
        }
    }

    @Override
    public List<Transaction> findByCompteId(UUID compteId) {
        String sql = "SELECT * FROM Transactions WHERE idCompte = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, compteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapToObject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des transactions par compte", e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByType(TypeTransaction type) {
        String sql = "SELECT * FROM Transactions WHERE type = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapToObject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des transactions par type", e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM Transactions WHERE date BETWEEN ? AND ?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapToObject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des transactions par intervalle de dates", e);
        }
        return transactions;
    }

    private Transaction mapToObject(ResultSet rs) throws SQLException {
        return new Transaction(
                UUID.fromString(rs.getString("id")),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getDouble("montant"),
                TypeTransaction.valueOf(rs.getString("type")),
                rs.getString("lieu"),
                UUID.fromString(rs.getString("idCompte"))
        );
    }
}
