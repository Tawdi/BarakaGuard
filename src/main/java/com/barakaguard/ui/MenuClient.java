package main.java.com.barakaguard.ui;

import main.java.com.barakaguard.entity.client.Client;
import main.java.com.barakaguard.service.ClientService;
import main.java.com.barakaguard.service.interfaces.IClientService;
import main.java.com.barakaguard.util.InputUtil;

import java.util.UUID;

public class MenuClient {

    private final IClientService clientService = new ClientService();

    public void afficher() {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n=== MENU CLIENT ===");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Lister les clients");
            System.out.println("3. Rechercher un client par ID");
            System.out.println("4. Rechercher un client par nom");
            System.out.println("5. Modifier un client");
            System.out.println("6. Supprimer un client");
            System.out.println("0. Retour");

            int choix = InputUtil.readInt("Choix", 0, 6);

            switch (choix) {
                case 1 -> ajouterClient();
                case 2 -> listerClients();
                case 3 -> rechercherClientParId();
                case 4 -> rechercherClientParNom();
                case 5 -> modifierClient();
                case 6 -> supprimerClient();
                case 0 -> retour = true;
                default -> System.out.println("❌ Choix invalide !");
            }
        }
    }

    private void ajouterClient() {
        String nom = InputUtil.readString("Nom: ");
        String email = InputUtil.readEmail("Email: ");
        UUID id = UUID.randomUUID();

        Client client = new Client(id, nom, email);
        clientService.add(client);
        System.out.println("✅ Client ajouté avec ID: " + id);
    }

    private void listerClients() {
        clientService.getAll().forEach(c -> System.out.println(c.id() + " | " + c.nom() + " | " + c.email()));
    }

    private void rechercherClientParId() {
        UUID id = InputUtil.readUUID("ID du client: ");
        clientService.getById(id).ifPresentOrElse(
                c -> System.out.println(c.id() + " | " + c.nom() + " | " + c.email()),
                () -> System.out.println("❌ Client non trouvé !"));
    }

    private void rechercherClientParNom() {
        String nom = InputUtil.readString("Nom du client: ");
        clientService.getAll().stream()
                .filter(c -> c.nom().equalsIgnoreCase(nom))
                .findFirst()
                .ifPresentOrElse(
                        c -> System.out.println(c.id() + " | " + c.nom() + " | " + c.email()),
                        () -> System.out.println("❌ Aucun client trouvé avec ce nom !"));
    }

    private void modifierClient() {
        UUID id = InputUtil.readUUID("ID du client à modifier: ");

        clientService.getById(id).ifPresentOrElse(client -> {
            String nom = InputUtil.readString("Nouveau nom (laisser vide pour conserver): ");
            String email = InputUtil.readEmail("Nouvel email (laisser vide pour conserver): ");

            Client updated = new Client(
                    id,
                    nom.isBlank() ? client.nom() : nom,
                    email.isBlank() ? client.email() : email);

            clientService.update(updated);
            System.out.println("✅ Client mis à jour !");
        }, () -> System.out.println("❌ Client introuvable !"));
    }

    private void supprimerClient() {
        UUID id = InputUtil.readUUID("ID du client à supprimer: ");
        clientService.delete(id);
        System.out.println("✅ Client supprimé (si existait).");
    }

}
