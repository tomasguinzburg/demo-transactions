package com.tomasguinzburg.demo.core.repositories;

import com.tomasguinzburg.demo.core.transactions.models.Transaction;

import java.util.List;
import java.util.NoSuchElementException;

public interface TransactionRepository {

    Long save(Transaction t);
    List<Transaction> getAll();
    Transaction get(long ID);
    Transaction get(String reference) throws NoSuchElementException;

}
