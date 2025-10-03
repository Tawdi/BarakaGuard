package main.java.com.barakaguard.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.entity.transaction.TypeTransaction;

public interface TransactionDAO extends BaseDAO<Transaction, UUID> {

    List<Transaction> findByCompteId(UUID compteId);

    Map<UUID ,List<Transaction>> findByClientId(UUID clientId);

    List<Transaction> findByType(TypeTransaction type);

    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end);
}
