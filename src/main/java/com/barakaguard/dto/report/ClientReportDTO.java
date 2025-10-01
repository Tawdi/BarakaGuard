package main.java.com.barakaguard.dto.report;

import java.util.UUID;

public record ClientReportDTO(UUID clientId, String nom, int nbComptes, double soldeTotal, long nbTransactions) {
}
