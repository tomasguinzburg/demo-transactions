package com.tomasguinzburg.demo.transactions;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TransactionsRepository {
    private final int CAPACITY = 100000000;

    private final AtomicLong idSequence;
    private final Map<Long, TransactionEntity> fakeStorage;

    @Inject
    public TransactionsRepository() {
        idSequence = new AtomicLong(0);
        fakeStorage = new ConcurrentHashMap<>(CAPACITY);
    }

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
        fakeStorage.put(ID, entity);
        return ID;
    }

    public List<TransactionEntity> getAll() {
        return fakeStorage.entrySet()
                          .stream()
                          .map(Map.Entry::getValue)
                          .collect(Collectors.toList());
    }

    public TransactionEntity getByID(long ID) {
        return fakeStorage.get(ID);
    }
}
