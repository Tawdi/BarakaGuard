package main.java.com.barakaguard.entity.compte;

import java.util.UUID;

public final class CompteCourant extends Compte {

    private double decouvert;

    public CompteCourant(UUID id, String numero, double solde, UUID idClient, double decouvertAutorise) {
        super(id, numero, solde, idClient);
        setDecouvert(decouvertAutorise);
    }

    public void setDecouvert(double decouvertAutorise) {
        this.decouvert = decouvertAutorise;
    }

    public double getDecouvertAutorise() {
        return decouvert;
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "id=" + getId() +
                ", numero='" + getNumero() + '\'' +
                ", solde=" + getSolde() +
                ", idClient=" + getIdClient() +
                ", decouvertAutorise=" + decouvert +
                '}';
    }
}
