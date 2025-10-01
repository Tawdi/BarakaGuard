package main.java.com.barakaguard.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompteInactifDTO(UUID compteId, String numero, UUID clientId, LocalDateTime lastTransactionDate,
        long daysInactive) {
}
