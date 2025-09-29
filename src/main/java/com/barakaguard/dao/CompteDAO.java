package main.java.com.barakaguard.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import main.java.com.barakaguard.entity.compte.Compte;

public interface CompteDAO extends BaseDAO<Compte, UUID> {

    List<Compte> findByClientId(UUID clientId);

    Optional<Compte> findByNumero(String numero);
}
