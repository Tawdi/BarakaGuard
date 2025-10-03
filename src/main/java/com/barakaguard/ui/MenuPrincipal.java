package main.java.com.barakaguard.ui;

import main.java.com.barakaguard.service.ClientService;
import main.java.com.barakaguard.service.CompteService;
import main.java.com.barakaguard.service.RapportService;
import main.java.com.barakaguard.service.TransactionService;
import main.java.com.barakaguard.util.InputUtil;

public class MenuPrincipal {

    private final MenuClient menuClient = new MenuClient();
    private final MenuCompte menuCompte = new MenuCompte();
    private final MenuTransaction menuTransaction = new MenuTransaction();
    private final MenuRapport menuRapport;

    public MenuPrincipal(ClientService clientService, CompteService compteService,
            TransactionService transactionService) {
        RapportService rapportService = new RapportService(clientService, compteService, transactionService);
        this.menuRapport = new MenuRapport(rapportService);
    }

    public void afficher() {
        boolean quitter = false;

        while (!quitter) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Gérer les clients");
            System.out.println("2. Gérer les comptes");
            System.out.println("3. Gérer les transactions");
            System.out.println("4. Rapports et analyses");
            System.out.println("0. Quitter");
            System.out.print("Choix: ");

            int choix = InputUtil.readInt("Choix", 0, 4);

            switch (choix) {
                case 1 -> menuClient.afficher();
                case 2 -> menuCompte.afficher();
                case 3 -> menuTransaction.afficher();
                case 4 -> menuRapport.afficher();
                case 0 -> {
                    System.out.println("Au revoir !");
                    quitter = true;
                }
                default -> System.out.println("Choix invalide !");
            }
        }
    }
}
