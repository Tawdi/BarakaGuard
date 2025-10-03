package main.java.com.barakaguard.ui;

import java.util.Scanner;

import main.java.com.barakaguard.util.InputUtil;

public class MenuPrincipal {

    private final MenuClient menuClient = new MenuClient();
    private final MenuCompte menuCompte = new MenuCompte();
    private final MenuTransaction menuTransaction = new MenuTransaction();

    public void afficher() {
        boolean quitter = false;

        while (!quitter) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Gérer les clients");
            System.out.println("2. Gérer les comptes");
            System.out.println("3. Gérer les transactions");
            System.out.println("4. Quitter");
            System.out.print("Choix: ");

           int choix = InputUtil.readInt("Choix", 1, 4);

            switch (choix) {
                case 1 -> menuClient.afficher();
                case 2 -> menuCompte.afficher();
                case 3 -> menuTransaction.afficher();
                case 4 -> {
                    System.out.println("Au revoir !");
                    quitter = true;
                }
                default -> System.out.println("Choix invalide !");
            }
        }
    }
}
