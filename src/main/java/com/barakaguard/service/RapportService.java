package main.java.com.barakaguard.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import main.java.com.barakaguard.dto.report.ClientReportDTO;
import main.java.com.barakaguard.dto.report.CompteInactifDTO;
import main.java.com.barakaguard.dto.report.MonthlyReportDTO;
import main.java.com.barakaguard.dto.report.TopClientDTO;
import main.java.com.barakaguard.dto.report.TransactionSuspiciousDTO;
import main.java.com.barakaguard.entity.compte.Compte;
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

        throw new UnsupportedOperationException("Unimplemented method 'monthlyReport'");
    }

    @Override
    public List<CompteInactifDTO> comptesInactifs(int seuilJours) {

        throw new UnsupportedOperationException("Unimplemented method 'comptesInactifs'");
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
