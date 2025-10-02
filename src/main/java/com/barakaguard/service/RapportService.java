package main.java.com.barakaguard.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import main.java.com.barakaguard.dto.report.*;
import main.java.com.barakaguard.util.*;
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

        List<TransactionSuspiciousDTO> result = new ArrayList<>();
        var allTrns = transactionService.getAll();

        if (allTrns.isEmpty()) {
            return List.of();
        }

        // montant
        result.addAll(detectMontantSuspicious(allTrns, montantSeuil));

        // lieu Inhabituel
        if (checkLieuInhabituel) {
            result.addAll(detectLieuRare(allTrns, 2));
        }

        // fréquence excessive par compte
        result.addAll(detectFrequenceExcessiveParCompte(allTrns, freqMaxOps, windowSeconds));

        return result;
    }

    @Override
    public ClientReportDTO clientReport(UUID clientId) {

        var client = clientService.getById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        var comptes = compteService.getByClientId(clientId);

        double soldeTotal = comptes.stream()
                .mapToDouble(Compte::getSolde)
                .sum();

        long nbTransactions = comptes.stream()
                .flatMap(c -> transactionService.getByCompteId(c.getId()).stream())
                .count();

        return new ClientReportDTO(
                clientId,
                client.nom(),
                comptes.size(),
                soldeTotal,
                nbTransactions);
    }

    private List<TransactionSuspiciousDTO> detectMontantSuspicious(List<Transaction> trnsList, double montantSeuil) {
        return trnsList.stream()
                .filter(t -> t.montant() > montantSeuil)
                .map(t -> new TransactionSuspiciousDTO(
                        t.id(), t.idCompte(), t.montant(), t.date(), t.type(), "MONTANT_SUPERIEUR_SEUIL"))
                .toList();

    }

    private List<TransactionSuspiciousDTO> detectLieuRare(List<Transaction> trnsList, int minCount) {

        var freqParCompte = trnsList.stream()
                .collect(Collectors.groupingBy(Transaction::idCompte));

        return trnsList.stream()
                .filter(t -> {
                    var pastTransactions = freqParCompte.get(t.idCompte());
                    String country = CountryExtractor.extractCountry(t.lieu());
                    long count = pastTransactions.stream()
                            .filter(pt -> CountryExtractor.extractCountry(pt.lieu()).equals(country))
                            .count();
                    return count < minCount;
                })
                .map(t -> new TransactionSuspiciousDTO(
                        t.id(), t.idCompte(), t.montant(), t.date(), t.type(),
                        "LIEU_INHABITUEL: " + CountryExtractor.extractCountry(t.lieu())))
                .toList();

    }

    private List<TransactionSuspiciousDTO> detectFrequenceExcessiveParCompte(
            List<Transaction> transactions, int freqMaxOps, long windowSeconds) {

        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::idCompte)) // Map<UUID, List<Transaction>>
                .entrySet().stream() // Stream<Map.Entry<UUID, List<Transaction>>>
                .flatMap(entry -> {
                    List<Transaction> sortedTr = entry.getValue().stream()
                            .sorted(Comparator.comparing(Transaction::date))
                            .toList();

                    return IntStream.range(0, sortedTr.size())
                            .mapToObj(i -> {
                                long count = sortedTr.stream()
                                        .filter(t -> Math.abs(Duration.between(t.date(), sortedTr.get(i).date())
                                                .getSeconds()) <= windowSeconds)
                                        .count();

                                if (count > freqMaxOps) {
                                    return new TransactionSuspiciousDTO(
                                            sortedTr.get(i).id(),
                                            entry.getKey(),
                                            sortedTr.get(i).montant(),
                                            sortedTr.get(i).date(),
                                            sortedTr.get(i).type(),
                                            "FREQUENCE_EXCESSIVE");
                                } else {
                                    return null;
                                }
                            })
                            .filter(dto -> dto != null);
                })
                .toList();
    }

}
