package main.java.com.barakaguard.service.interfaces;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import main.java.com.barakaguard.dto.transaction.TransactionFilter;
import main.java.com.barakaguard.entity.transaction.Transaction;
import main.java.com.barakaguard.entity.transaction.TypeTransaction;

public interface ITransactionService extends BaseService<Transaction, UUID> {

    public Map<TypeTransaction, List<Transaction>> groupByType();

    public Map<Integer, List<Transaction>> groupByYear();

    public Map<String, List<Transaction>> groupByMonth();

    public List<Transaction> getAll(TransactionFilter filter);

    public List<Transaction> getByCompteId(UUID id);
}
