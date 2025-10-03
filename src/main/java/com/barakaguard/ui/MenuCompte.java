package main.java.com.barakaguard.ui;

import main.java.com.barakaguard.entity.compte.Compte;
import main.java.com.barakaguard.entity.compte.CompteCourant;
import main.java.com.barakaguard.entity.compte.CompteEpargne;
import main.java.com.barakaguard.service.CompteService;
import main.java.com.barakaguard.service.interfaces.ICompteService;
import main.java.com.barakaguard.util.Generator;
import main.java.com.barakaguard.util.InputUtil;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MenuCompte {

    private final ICompteService compteService = new CompteService();

    private final String PREFIX_CODE = "CPT";
    private final int NUMBER_CODE_SIZE = 10;

    public void afficher() {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n=== MENU COMPTE ===");
            System.out.println("1. Créer un compte courant");
            System.out.println("2. Créer un compte épargne");
            System.out.println("3. Mettre à jour un compte (solde, découvert, taux)");
            System.out.println("4. Supprimer un compte");
            System.out.println("5. Rechercher comptes d’un client");
            System.out.println("6. Rechercher un compte par numéro");
            System.out.println("7. Voir compte avec solde max / min");
            System.out.println("0. Retour");

            int choix = InputUtil.readInt("Choix", 0, 7);

            switch (choix) {
                case 1 -> creerCompteCourant();
                case 2 -> creerCompteEpargne();
                case 3 -> mettreAJourCompte();
                case 4 -> supprimerCompte();
                case 5 -> listerComptesParClient();
                case 6 -> rechercherCompteParNumero();
                case 7 -> afficherCompteMaxMin();
                case 0 -> retour = true;
            }
        }
    }

    private void creerCompteCourant() {
        UUID clientId = InputUtil.readUUID("ID du client: ");
        String numero = Generator.generateCodeNumber(PREFIX_CODE, NUMBER_CODE_SIZE);
        double solde = InputUtil.readDouble("Solde initial: ");
        double decouvert = InputUtil.readDouble("Découvert autorisé: ");

        CompteCourant compte = new CompteCourant(UUID.randomUUID(), numero, solde, clientId, decouvert);
        compteService.add(compte);
        System.out.println("✅ Compte courant créé avec ID: " + compte.getId());
    }

    private void creerCompteEpargne() {
        UUID clientId = InputUtil.readUUID("ID du client: ");
        String numero = Generator.generateCodeNumber(PREFIX_CODE, NUMBER_CODE_SIZE);
        double solde = InputUtil.readDouble("Solde initial: ");
        double taux = InputUtil.readDouble("Taux d’intérêt (%): ");

        CompteEpargne compte = new CompteEpargne(UUID.randomUUID(), numero, solde, clientId, taux);
        compteService.add(compte);
        System.out.println("✅ Compte épargne créé avec ID: " + compte.getId());
    }

    private void mettreAJourCompte() {
        UUID id = InputUtil.readUUID("ID du compte à modifier: ");
        Optional<Compte> compteOpt = compteService.getById(id);

        if (compteOpt.isEmpty()) {
            System.out.println("❌ Compte introuvable !");
            return;
        }

        Compte compte = compteOpt.get();
        Double nouveauSolde = InputUtil.readDoubleOptional("Nouveau solde");
        if (nouveauSolde != null) {
            compte.setSolde(nouveauSolde);
        }

        if (compte instanceof CompteCourant cc) {
            Double newDecouvert = InputUtil.readDoubleOptional("Nouveau découvert autorisé");
            if (newDecouvert != null)
                cc.setDecouvert(newDecouvert);
        } else if (compte instanceof CompteEpargne ce) {
            Double newTaux = InputUtil.readDoubleOptional("Nouveau taux d’intérêt");
            if (newTaux != null)
                ce.setTauxInteret(newTaux);
        }

        compteService.update(compte);
        System.out.println("✅ Compte mis à jour !");
    }

    private void supprimerCompte() {
        UUID id = InputUtil.readUUID("ID du compte à supprimer: ");
        compteService.delete(id);
        System.out.println("✅ Compte supprimé (si existait).");
    }

    private void listerComptesParClient() {
        UUID clientId = InputUtil.readUUID("ID du client: ");
        var comptes = compteService.getByClientId(clientId);

        if (comptes.isEmpty()) {
            System.out.println("❌ Aucun compte trouvé pour ce client.");
        } else {
            comptes.forEach(c -> System.out.println(c.getId() + " | " + c.getNumero() + " | " + c.getSolde()));
        }
    }

    private void rechercherCompteParNumero() {
        String numero = InputUtil.readString("Numéro du compte: ");
        compteService.getByNumero(numero).ifPresentOrElse(
                c -> System.out.println(c.getId() + " | " + c.getNumero() + " | " + c.getSolde()),
                () -> System.out.println("❌ Compte non trouvé !"));
    }

    private void afficherCompteMaxMin() {
        Map<String, Optional<Compte>> result = compteService.getCompteMaxMinSolde();

        result.get("max").ifPresent(c -> System.out.println("💰 Compte avec solde maximum : " +
                c.getNumero() + " | " + c.getSolde()));

        result.get("min").ifPresent(c -> System.out.println("💸 Compte avec solde minimum : " +
                c.getNumero() + " | " + c.getSolde()));
    }
}
