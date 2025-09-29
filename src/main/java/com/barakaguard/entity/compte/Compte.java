package main.java.com.barakaguard.entity.compte;

import java.util.UUID;

public sealed abstract class Compte
        permits CompteCourant, CompteEpargne {

    protected final UUID id;
    protected final String numero;
    protected double solde;
    protected final UUID idClient;

    public Compte(UUID id, String numero, double solde, UUID idClient) {
        this.id = id;
        this.numero = numero;
        this.solde = solde;
        this.idClient = idClient;
    }

    public UUID getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public double getSolde() {
        return solde;
    }

    public UUID getIdClient() {
        return idClient;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", solde=" + solde +
                ", idClient=" + idClient +
                '}';
    }
}
