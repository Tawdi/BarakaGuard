package main.java.com.barakaguard.service.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import main.java.com.barakaguard.entity.compte.Compte;

public interface ICompteService extends BaseService<Compte, UUID> {

    public List<Compte> getByClientId(UUID clientId);

    public Optional<Compte> getByNumero(String numero);

    public Optional<Compte> getCompteMaxSolde();

    public Optional<Compte> getCompteMinSolde();

    public Map<String, Optional<Compte>> getCompteMaxMinSolde();
}
