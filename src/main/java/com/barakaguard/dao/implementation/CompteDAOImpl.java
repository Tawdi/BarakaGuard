package main.java.com.barakaguard.dao.implementation;

import main.java.com.barakaguard.dao.CompteDAO;
import main.java.com.barakaguard.entity.compte.Compte;
import main.java.com.barakaguard.entity.compte.CompteCourant;
import main.java.com.barakaguard.entity.compte.CompteEpargne;
import main.java.com.barakaguard.exception.DAOException;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class CompteDAOImpl implements CompteDAO {

    private final Connection connection;

    public CompteDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Compte compte) {
        String sql = "INSERT INTO Compte (id, numero, solde, idClient, typeCompte, decouvertAutorise, tauxInteret) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, compte.getId());
            stmt.setString(2, compte.getNumero());
            stmt.setDouble(3, compte.getSolde());
            stmt.setObject(4, compte.getIdClient());

            if (compte instanceof CompteCourant cc) {
                stmt.setString(5, "courant");
                stmt.setDouble(6, cc.getDecouvertAutorise());
                stmt.setNull(7, Types.NUMERIC);
            } else if (compte instanceof CompteEpargne ce) {
                stmt.setString(5, "epargne");
                stmt.setNull(6, Types.NUMERIC);
                stmt.setDouble(7, ce.getTauxInteret());
            } else {
                throw new DAOException("Type de compte inconnu: " + compte.getClass());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de l'insertion du compte", e);
        }
    }

    @Override
    public Optional<Compte> findById(UUID id) {
        String sql = "SELECT * FROM Compte WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapToObject(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du compte par ID", e);
        }
    }

    @Override
    public List<Compte> findAll() {
        String sql = "SELECT * FROM Compte";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Compte> comptes = new ArrayList<>();
            while (rs.next()) {
                comptes.add(mapToObject(rs));
            }

            return comptes.stream()
                          .sorted(Comparator.comparing(Compte::getNumero))
                          .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de tous les comptes", e);
        }
    }

    @Override
    public void update(Compte compte) {
        String sql = "UPDATE Compte SET solde = ?, decouvertAutorise = ?, tauxInteret = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, compte.getSolde());

            if (compte instanceof CompteCourant cc) {
                stmt.setDouble(2, cc.getDecouvertAutorise());
                stmt.setNull(3, Types.NUMERIC);
            } else if (compte instanceof CompteEpargne ce) {
                stmt.setNull(2, Types.NUMERIC);
                stmt.setDouble(3, ce.getTauxInteret());
            } else {
                throw new DAOException("Type de compte inconnu lors de la mise à jour");
            }

            stmt.setObject(4, compte.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du compte", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM Compte WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du compte", e);
        }
    }

    @Override
    public List<Compte> findByClientId(UUID clientId) {
        String sql = "SELECT * FROM Compte WHERE idClient = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Compte> comptes = new ArrayList<>();
                while (rs.next()) {
                    comptes.add(mapToObject(rs));
                }
                return comptes;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des comptes par client", e);
        }
    }

    @Override
    public Optional<Compte> findByNumero(String numero) {
        String sql = "SELECT * FROM Compte WHERE numero = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapToObject(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du compte par numéro", e);
        }
    }

    private Compte mapToObject(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String numero = rs.getString("numero");
        double solde = rs.getDouble("solde");
        UUID idClient = (UUID) rs.getObject("idClient");
        String typeCompte = rs.getString("typeCompte");

        return switch (typeCompte) {
            case "courant" -> new CompteCourant(
                    id,
                    numero,
                    solde,
                    idClient,
                    rs.getDouble("decouvertAutorise")
            );
            case "epargne" -> new CompteEpargne(
                    id,
                    numero,
                    solde,
                    idClient,
                    rs.getDouble("tauxInteret")
            );
            default -> throw new DAOException("Type de compte inconnu: " + typeCompte);
        };
    }
}
