package main.java.com.barakaguard.service.interfaces;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import main.java.com.barakaguard.dto.report.*;

public interface IRapportService {

    List<TopClientDTO> topClientsByBalance(int topN);

    MonthlyReportDTO monthlyReport(int year, int month);

    List<CompteInactifDTO> comptesInactifs(int seuilJours);

    List<TransactionSuspiciousDTO> detectSuspicious(double montantSeuil,
            boolean checkLieuInhabituel,
            int freqMaxOps,
            long windowSeconds);

    ClientReportDTO clientReport(UUID clientId);

    // // CSV / JSON
    // void exportMonthlyReportCsv(int year, int month, OutputStream out) throws
    // IOException;

    // void exportMonthlyReportJson(int year, int month, OutputStream out) throws
    // IOException;
}
