package main.java.com.barakaguard.util.mock;

import main.java.com.barakaguard.entity.client.Client;
import main.java.com.barakaguard.entity.compte.Compte;
import main.java.com.barakaguard.entity.compte.CompteCourant;
import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.entity.transaction.TypeTransaction;
import main.java.com.barakaguard.service.ClientService;
import main.java.com.barakaguard.service.CompteService;
import main.java.com.barakaguard.service.TransactionService;
import main.java.com.barakaguard.util.Generator;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class DataMocker {

    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();
    private final Random random = new Random();

    public DataMocker() {
    }

    public void generateData() {
        for (int i = 1; i <= 10; i++) {
            Client client = new Client(
                    UUID.randomUUID(),
                    "Client" + i,
                    "client" + i + "@example.com");
            clientService.add(client);

            int comptesCount = 2 + random.nextInt(3); // 2 to 4 comptes per client
            for (int j = 1; j <= comptesCount; j++) {
                Compte compte = new CompteCourant(
                        UUID.randomUUID(),
                        Generator.generateCodeNumber("CPT", 10),
                        random.nextDouble() * 10000,
                        client.id(), 1000);
                compteService.add(compte);

                for (int k = 1; k <= 10; k++) {
                    Transaction tx = new Transaction(
                            UUID.randomUUID(),
                            LocalDateTime.now().minusDays(random.nextInt(30)).withHour(random.nextInt(24)) .withMinute(random.nextInt(60)),

                            ( k%3 == 1 ? -1 : 1) * random.nextDouble() * 1000  ,
                            randomType(k),
                            randomLieu(),
                            compte.getId());
                    transactionService.add(tx);
                }
            }
        }
        System.out.println("✅ Mock data generated successfully!");
    }

    private TypeTransaction randomType(int i) {
        String[] types = { "VERSEMENT", "RETRAIT", "VIREMENT" };
        return TypeTransaction.valueOf(types[ i % types.length]);
    }

    private String randomLieu() {
        String[] villes = { "Casablanca", "Rabat", "Marrakech", "Fes", "Agadir", "Safi" };
        int n = random.nextInt(200) + 1;
        return villes[random.nextInt(villes.length)] + " n" + n + ", Maroc";
    }
}
