package main.java.com.barakaguard.ui;

import main.java.com.barakaguard.service.RapportService;
import main.java.com.barakaguard.util.ExportUtil;
import main.java.com.barakaguard.util.InputUtil;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
        var data = rapportService.topClientsByBalance(n);
        data.forEach(c -> System.out.printf("👤 %s | %s | Total: %.2f%n", c.nom(), c.email(), c.totalBalance()));

        String[] headers = { "Nom", "Email", "Solde" };
        List<String[]> rows = data.stream()
                .map(c -> new String[] { c.nom(), c.email(), String.valueOf(c.totalBalance()) })
                .toList();

        handleExport(headers, rows, "topClients");
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

        String[] headers = { "Type", "Nombre", "Volume" };
        List<String[]> rows = report.countByType().entrySet().stream()
                .map(e -> new String[] {
                        e.getKey().toString(),
                        String.valueOf(e.getValue()),
                        String.valueOf(report.volumeByType().getOrDefault(e.getKey(), 0.0))
                })
                .toList();

        handleExport(headers, rows, "monthlyReport_" + year + "_" + month);
    }

    private void comptesInactifs() {
        int jours = InputUtil.readInt("Seuil d'inactivité en jours: ", 1, 3650);
        var data = rapportService.comptesInactifs(jours);
        data.forEach(c -> System.out.printf("💤 Compte %s (Client %s) inactif depuis %d jours%n",
                c.numero(), c.clientId(), c.daysInactive()));

        String[] headers = { "Compte", "ClientId", "JoursInactifs" };
        List<String[]> rows = data.stream()
                .map(c -> new String[] { c.numero(), c.clientId().toString(), String.valueOf(c.daysInactive()) })
                .toList();

        handleExport(headers, rows, "comptesInactifs_" + jours);
    }

    private void detectSuspicious() {
        double seuil = InputUtil.readDouble("Montant seuil: ");
        boolean checkLieu = InputUtil.readYesNo("Vérifier lieux inhabituels ? (o/n): ");
        int freq = InputUtil.readInt("Nombre max d'opérations autorisées: ", 1, 1000);
        long window = InputUtil.readLong("Fenêtre en secondes: ");

        var data = rapportService.detectSuspicious(seuil, checkLieu, freq, window);
        data.forEach(s -> System.out.printf("⚠️ Suspicious: %s | Compte %s | %.2f | %s | %s%n",
                s.txId(), s.compteId(), s.montant(), s.date(), s.reason()));

        String[] headers = { "TransactionId", "CompteId", "Montant", "Date", "Raison" };
        List<String[]> rows = data.stream()
                .map(s -> new String[] {
                        s.txId().toString(),
                        s.compteId().toString(),
                        String.valueOf(s.montant()),
                        s.date().toString(),
                        s.reason()
                })
                .toList();

        handleExport(headers, rows, "suspicious");
    }

    private void clientReport() {
        String id = InputUtil.readString("UUID du client: ");
        try {
            var report = rapportService.clientReport(UUID.fromString(id));
            System.out.printf("👤 %s | %d comptes | Solde total: %.2f | %d transactions%n",
                    report.nom(), report.nbComptes(), report.soldeTotal(), report.nbTransactions());

            String[] headers = { "Nom", "NbComptes", "SoldeTotal", "NbTransactions" };
            List<String[]> rows = Arrays.asList(
                    new String[][] {
                            new String[] {
                                    report.nom(),
                                    String.valueOf(report.nbComptes()),
                                    String.valueOf(report.soldeTotal()),
                                    String.valueOf(report.nbTransactions())
                            }
                    });

            handleExport(headers, rows, "clientReport_" + id);
        } catch (Exception e) {
            System.out.println("❌ Client introuvable");
        }
    }

    private void handleExport(String[] headers, List<String[]> rows, String baseFileName) {
        if (InputUtil.readYesNo("Exporter ce rapport ? (o/n): ")) {
            int choix = InputUtil.readInt("1. CSV  2. JSON  3. Les deux", 1, 3);

            switch (choix) {
                case 1 -> ExportUtil.exportCSV(headers, rows, baseFileName + ".csv");
                case 2 -> ExportUtil.exportJSON(headers, rows, baseFileName + ".json");
                case 3 -> {
                    ExportUtil.exportCSV(headers, rows, baseFileName + ".csv");
                    ExportUtil.exportJSON(headers, rows, baseFileName + ".json");
                }
                default -> System.out.println("❌ Choix invalide.");
            }
        }
    }
}
