package main.java.com.barakaguard.service.interfaces;

import java.util.Optional;
import java.util.UUID;

import main.java.com.barakaguard.entity.client.Client;

public interface IClientService extends BaseService<Client, UUID> {

    Optional<Client> getByEmail(String email);
}
