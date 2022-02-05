package com.tomasguinzburg.demo.core.repository;

import com.tomasguinzburg.demo.core.transactions.Transaction;

import java.util.List;

public interface TransactionsRepository {

    Long save(Transaction t);
    List<Transaction> getAll();
    Transaction get(long ID);

}
