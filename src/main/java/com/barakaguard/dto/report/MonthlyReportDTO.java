package main.java.com.barakaguard.dto.report;

import java.util.Map;

import main.java.com.barakaguard.entity.transaction.TypeTransaction;

public record MonthlyReportDTO(int year, int month,
        Map<TypeTransaction, Long> countByType,
        Map<TypeTransaction, Double> volumeByType,
        long totalTransactions,
        double totalVolume) {
}
