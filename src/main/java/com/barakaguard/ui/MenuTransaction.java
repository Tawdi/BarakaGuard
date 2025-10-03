package main.java.com.barakaguard.ui;

import main.java.com.barakaguard.dto.transaction.TransactionFilter;
import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.entity.transaction.TypeTransaction;
import main.java.com.barakaguard.service.TransactionService;
import main.java.com.barakaguard.service.interfaces.ITransactionService;
import main.java.com.barakaguard.util.InputUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuTransaction {

    private final ITransactionService transactionService = new TransactionService();

    public void afficher() {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n=== MENU TRANSACTION ===");
            System.out.println("1. Versement");
            System.out.println("2. Retrait");
            System.out.println("3. Virement");
            System.out.println("4. Lister les transactions d’un compte");
            System.out.println("5. Lister les transactions d’un client");
            System.out.println("6. Lister les transactions ");
            System.out.println("0. Retour");

            int choix = InputUtil.readInt("Choix", 0, 6);

            switch (choix) {
                case 1 -> effectuerVersement();
                case 2 -> effectuerRetrait();
                case 3 -> effectuerVirement();
                case 4 -> listerTransactionsCompte();
                case 5 -> listerTransactionsClient();
                case 6 -> listerTransactionsFiltre();
                case 0 -> retour = true;
            }
        }
    }

    private void listerTransactionsFiltre() {
        List<Transaction> transactions;
        if (InputUtil.readYesNo("Voulez-vous appliquer un filtre ? (o/n): ")) {
            TransactionFilter filter = new TransactionFilter();

            filter.setMinMontant(InputUtil.readDoubleOptional("Montant minimum (laisser vide si aucun): "));
            filter.setMaxMontant(InputUtil.readDoubleOptional("Montant maximum (laisser vide si aucun): "));

            TypeTransaction[] types = TypeTransaction.values();
            String[] options = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                options[i] = types[i].name();
            }

            TypeTransaction type = InputUtil.readEnum("Type de transaction : ", TypeTransaction.class);
            filter.setType(type);

            // Conversion LocalDate -> LocalDateTime
            var startDate = InputUtil.readDateOptional("Date de début (yyyy-MM-dd ou vide): ");
            if (startDate != null) {
                filter.setStartDate(startDate.atStartOfDay());
            }

            var endDate = InputUtil.readDateOptional("Date de fin (yyyy-MM-dd ou vide): ");
            if (endDate != null) {
                filter.setEndDate(endDate.atTime(23, 59, 59));
            }

            String lieu = InputUtil.readString("Lieu (ou vide): ");
            if (lieu != null && !lieu.isBlank()) {
                filter.setLieu(lieu);
            }

            transactions = transactionService.getAll(filter);

        } else {
            transactions = transactionService.getAll();
        }

        // affichage
        if (transactions.isEmpty()) {
            System.out.println("⚠️ Aucune transaction trouvée.");
        } else {
            transactions.forEach(t -> System.out.println(
                    t.id() + " | " + t.type() + " | " + t.montant() + " | " + t.lieu() + " | " + t.date()));
        }
    }

    private void listerTransactionsCompte() {
        UUID compteId = InputUtil.readUUID("ID du compte: ");
        List<Transaction> transactions = transactionService.getByCompteId(compteId);

        if (transactions.isEmpty()) {
            System.out.println("⚠️ Aucune transaction pour ce compte.");
        } else {
            transactions.forEach(t -> System.out.println(t.id() + " | " + t.type() + " | " + t.montant()
                    + " | " + t.lieu() + " | " + t.date()));
        }
    }

    private void listerTransactionsClient() {
        UUID clientId = InputUtil.readUUID("ID du client: ");
        Map<UUID, List<Transaction>> transactionsByCompte = transactionService.getByClientId(clientId);

        if (transactionsByCompte.isEmpty()) {
            System.out.println("⚠️ Aucune transaction pour ce client.");
        } else {
            transactionsByCompte.forEach((compteId, transactions) -> {
                System.out.println("\nCompte: " + compteId);
                transactions.forEach(t -> System.out.println("   " + t.id() + " | " + t.type()
                        + " | " + t.montant() + " | " + t.lieu() + " | " + t.date()));
            });
        }
    }

    private void effectuerVirement() {
        UUID sourceId = InputUtil.readUUID("ID du compte source: ");
        UUID destId = InputUtil.readUUID("ID du compte destinataire: ");
        double montant = InputUtil.readDouble("Montant à transférer: ");
        String lieu = InputUtil.readString("Lieu (ex: Safi , Maroc): ");

        try {
            transactionService.virement(sourceId, destId, montant, lieu);
            System.out.println("✅ Virement effectué avec succès.");
        } catch (Exception e) {
            System.err.println("❌ Échec du virement : " + e.getMessage());
        }
    }

    private void effectuerVersement() {
        UUID compteId = InputUtil.readUUID("ID du compte : ");
        double montant = InputUtil.readDouble("Montant à verser: ");
        String lieu = InputUtil.readString("Lieu (ex: Safi , Maroc): ");

        try {
            transactionService.versement(compteId, montant, lieu);
            System.out.println("✅ Versement effectué avec succès.");
        } catch (Exception e) {
            System.err.println("❌ Échec du Versement : " + e.getMessage());
        }
    }

    private void effectuerRetrait() {
        UUID compteId = InputUtil.readUUID("ID du compte : ");
        double montant = InputUtil.readDouble("Montant à retirer: ");
        String lieu = InputUtil.readString("Lieu (ex: Safi , Maroc): ");

        try {
            transactionService.retrait(compteId, montant, lieu);
            System.out.println("✅ Retrait effectué avec succès.");
        } catch (Exception e) {
            System.err.println("❌ Échec du Retrait : " + e.getMessage());
        }
    }

}
