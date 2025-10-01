package main.java.com.barakaguard.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

import main.java.com.barakaguard.entity.transaction.TypeTransaction;

public record TransactionSuspiciousDTO(UUID txId, UUID compteId, double montant, LocalDateTime date,
        TypeTransaction type, String reason) {
}
