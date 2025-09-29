package main.java.com.barakaguard.entity.compte;

import java.util.UUID;

public final class CompteEpargne extends Compte {

    private double tauxInteret;

    public CompteEpargne(UUID id, String numero, double solde, UUID idClient, double tauxInteret) {
        super(id, numero, solde, idClient);
        setTauxInteret(tauxInteret);
    }

    public double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }


    @Override
    public String toString() {
        return "CompteEpargne{" +
                "id=" + getId() +
                ", numero='" + getNumero() + '\'' +
                ", solde=" + getSolde() +
                ", idClient=" + getIdClient() +
                ", tauxInteret=" + tauxInteret +
                '}';
    }
}
