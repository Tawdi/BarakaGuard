package main.java.com.barakaguard.dto.report;

import java.util.Map;

public record MonthlyReportDTO(int year, int month,
        Map<String, Long> countByType,
        Map<String, Double> volumeByType,
        long totalTransactions,
        double totalVolume) {
}
