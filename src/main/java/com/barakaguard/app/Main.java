package main.java.com.barakaguard.app;

import main.java.com.barakaguard.service.ClientService;
import main.java.com.barakaguard.service.CompteService;
import main.java.com.barakaguard.service.TransactionService;
import main.java.com.barakaguard.ui.MenuPrincipal;

public class Main {

    public static void main(String[] args) {
        MenuPrincipal menu = new MenuPrincipal(new ClientService(), new CompteService(), new TransactionService());
        menu.afficher();
    }
}
