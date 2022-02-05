package com.tomasguinzburg.demo.core.transactions;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;

import java.util.List;

public interface TransactionsService {

    Long create(Transaction transaction) throws ValidationException;
    Transaction get(Long ID);
    List<Transaction> getAll();
    List<Transaction> getByQuery(TransactionQuery filter);

}
