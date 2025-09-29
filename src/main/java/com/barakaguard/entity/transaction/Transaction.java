package main.java.com.barakaguard.entity.transaction;

import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
        UUID id,
        LocalDateTime date,
        double montant,
        TypeTransaction type,
        String lieu,
        UUID idCompte) {
}
