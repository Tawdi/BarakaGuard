package main.java.com.barakaguard.ui;

import main.java.com.barakaguard.service.RapportService;
import main.java.com.barakaguard.util.InputUtil;

import java.time.LocalDate;
import java.util.UUID;

public class MenuRapport {

    private final RapportService rapportService;

    public MenuRapport(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    public void afficher() {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n=== MENU RAPPORTS ===");
            System.out.println("1. Top N clients par solde");
            System.out.println("2. Rapport mensuel");
            System.out.println("3. Comptes inactifs");
            System.out.println("4. Détection de transactions suspectes");
            System.out.println("5. Rapport par client");
            System.out.println("0. Retour");
            int choix = InputUtil.readInt("Choix", 0, 5);

            switch (choix) {
                case 1 -> topClients();
                case 2 -> monthlyReport();
                case 3 -> comptesInactifs();
                case 4 -> detectSuspicious();
                case 5 -> clientReport();
                case 0 -> retour = true;
                default -> System.out.println("❌ Choix invalide");
            }
        }
    }

    private void topClients() {
        int n = InputUtil.readInt("Combien de clients afficher ?", 1, 50);
        rapportService.topClientsByBalance(n)
                .forEach(c -> System.out.printf("👤 %s | %s | Total: %.2f%n", c.nom(), c.email(), c.totalBalance()));
    }

    private void monthlyReport() {
        int year = InputUtil.readInt("Année (ex: 2025): ", 2000, LocalDate.now().getYear());
        int month = InputUtil.readInt("Mois (1-12): ", 1, 12);

        var report = rapportService.monthlyReport(year, month);
        System.out.println("📊 Rapport " + year + "-" + month);
        System.out.println("Transactions par type: " + report.countByType());
        System.out.println("Volumes par type: " + report.volumeByType());
        System.out.println("Total transactions: " + report.totalTransactions());
        System.out.println("Volume total: " + report.totalVolume());
    }

    private void comptesInactifs() {
        int jours = InputUtil.readInt("Seuil d'inactivité en jours: ", 1, 3650);
        rapportService.comptesInactifs(jours)
                .forEach(c -> System.out.printf("💤 Compte %s (Client %s) inactif depuis %d jours%n",
                        c.numero(), c.clientId(), c.daysInactive()));
    }

    private void detectSuspicious() {
        double seuil = InputUtil.readDouble("Montant seuil: ");
        boolean checkLieu = InputUtil.readYesNo("Vérifier lieux inhabituels ? (o/n): ");
        int freq = InputUtil.readInt("Nombre max d'opérations autorisées: ", 1, 1000);
        long window = InputUtil.readLong("Fenêtre en secondes: ");

        rapportService.detectSuspicious(seuil, checkLieu, freq, window)
                .forEach(s -> System.out.printf("⚠️ Suspicious: %s | Compte %s | %.2f | %s | %s%n",
                        s.txId(), s.compteId(), s.montant(), s.date(), s.reason()));
    }

    private void clientReport() {
        String id = InputUtil.readString("UUID du client: ");
        try {
            var report = rapportService.clientReport(UUID.fromString(id));
            System.out.printf("👤 %s | %d comptes | Solde total: %.2f | %d transactions%n",
                    report.nom(), report.nbComptes(), report.soldeTotal(), report.nbTransactions());
        } catch (Exception e) {
            System.out.println("❌ Client introuvable");
        }
    }
}
