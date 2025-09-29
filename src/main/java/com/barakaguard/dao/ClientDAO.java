package main.java.com.barakaguard.dao;

import java.util.Optional;
import java.util.UUID;

import main.java.com.barakaguard.entity.client.Client;

public interface ClientDAO extends BaseDAO<Client, UUID> {

    Optional<Client> findByEmail(String email);

}
