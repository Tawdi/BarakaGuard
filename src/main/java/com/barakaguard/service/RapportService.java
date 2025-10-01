package main.java.com.barakaguard.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import main.java.com.barakaguard.dto.report.ClientReportDTO;
import main.java.com.barakaguard.dto.report.CompteInactifDTO;
import main.java.com.barakaguard.dto.report.MonthlyReportDTO;
import main.java.com.barakaguard.dto.report.TopClientDTO;
import main.java.com.barakaguard.dto.report.TransactionSuspiciousDTO;
import main.java.com.barakaguard.dto.transaction.TransactionFilter;
import main.java.com.barakaguard.entity.compte.Compte;
import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.service.interfaces.IRapportService;

public class RapportService implements IRapportService {

    private final ClientService clientService;
    private final CompteService compteService;
    private final TransactionService transactionService;

    public RapportService(ClientService clientService,
            CompteService compteService,
            TransactionService transactionService) {
        this.clientService = clientService;
        this.compteService = compteService;
        this.transactionService = transactionService;
    }

    @Override
    public List<TopClientDTO> topClientsByBalance(int topN) {

        var clients = clientService.getAll();

        return clients
                .stream()
                .map(c -> {
                    double total = compteService.getByClientId(c.id()).stream().mapToDouble(Compte::getSolde).sum();

                    return new TopClientDTO(c.id(), c.nom(), c.email(), total);

                })
                .sorted(Comparator.comparingDouble(TopClientDTO::totalBalance).reversed())
                .limit(topN)
                .toList();

    }

    @Override
    public MonthlyReportDTO monthlyReport(int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDT = start.plusMonths(1).atStartOfDay();

        var filters = new TransactionFilter();
        filters.setStartDate(startDT);
        filters.setEndDate(endDT);

        var tr = transactionService.getAll(filters);

        var countByType = tr.stream().collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));

        var volumeBytype = tr.stream()
                .collect(Collectors.groupingBy(Transaction::type, Collectors.summingDouble(Transaction::montant)));
        var totalItem = tr.size();
        var totalVolume = tr.stream().mapToDouble(Transaction::montant).sum();

        return new MonthlyReportDTO(year, month, countByType, volumeBytype, totalItem, totalVolume);
    }

    @Override
    public List<CompteInactifDTO> comptesInactifs(int seuilJours) {

        var comptes = compteService.getAll();

        return comptes.stream()
                .map(
                        c -> {
                            var lastTrDate = transactionService
                                    .getByCompteId(c.getId())
                                    .stream()
                                    .map(Transaction::date)
                                    .max(LocalDateTime::compareTo)
                                    .orElse(null);

                            var daysInactive = (lastTrDate == null)
                                    ? Long.MAX_VALUE
                                    : Duration.between(lastTrDate, LocalDateTime.now()).toDays();
                            return new CompteInactifDTO(c.getId(), c.getNumero(), c.getIdClient(), lastTrDate,
                                    daysInactive);
                        })
                .filter(e -> e.lastTransactionDate() != null && e.daysInactive() >= seuilJours)
                .toList();
    }

    @Override
    public List<TransactionSuspiciousDTO> detectSuspicious(double montantSeuil, boolean checkLieuInhabituel,
            int freqMaxOps, long windowSeconds) {

        throw new UnsupportedOperationException("Unimplemented method 'detectSuspicious'");
    }

    @Override
    public ClientReportDTO clientReport(UUID clientId) {

        throw new UnsupportedOperationException("Unimplemented method 'clientReport'");
    }
}
