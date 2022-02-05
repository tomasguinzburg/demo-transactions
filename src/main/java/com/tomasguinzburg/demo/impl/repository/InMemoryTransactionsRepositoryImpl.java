package com.tomasguinzburg.demo.impl.repository;

import com.tomasguinzburg.demo.core.transactions.Transaction;
import com.tomasguinzburg.demo.core.repository.TransactionsRepository;
import com.tomasguinzburg.demo.impl.repository.entities.TransactionEntity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
public class InMemoryTransactionsRepositoryImpl implements TransactionsRepository {

    private final AtomicLong idSequence;
    private final Map<Long, TransactionEntity> memoryStorage;

    @Inject
    public InMemoryTransactionsRepositoryImpl(Integer capacity) {
        idSequence = new AtomicLong(0);
        memoryStorage = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public Long save(Transaction t) {
        Long ID = idSequence.incrementAndGet();
        TransactionEntity entity = TransactionEntity.builder()
                                                    .ID(ID)
                                                    .reference(t.getReference())
                                                    .accountIban(t.getAccountIban())
                                                    .amount(t.getAmount())
                                                    .fee(t.getFee())
                                                    .date(t.getDate())
                                                    .description(t.getDescription())
                                                    .build();
        memoryStorage.put(ID, entity);
        return ID;
    }

    //TODO: it's definitely better to just build or import an object mapper, but meanwhile we just use a lambda
    @Override
    public List<Transaction> getAll() {
        return memoryStorage.entrySet()
                          .stream()
                          .map(Map.Entry::getValue)
                          .map(e -> Transaction.builder()
                                               .reference(e.getReference())
                                               .accountIban(e.getAccountIban())
                                               .date(e.getDate())
                                               .amount(e.getAmount())
                                               .fee(e.getFee())
                                               .description(e.getDescription())
                                               .build())
                          .collect(Collectors.toList());
    }

    @Override
    public Transaction get(long ID) {
        TransactionEntity entity =  memoryStorage.get(ID);
        return Transaction.builder()
                          .reference(entity.getReference())
                          .accountIban(entity.getAccountIban())
                          .amount(entity.getAmount())
                          .fee(entity.getFee())
                          .date(entity.getDate())
                          .description(entity.getDescription())
                          .build();
    }
}
