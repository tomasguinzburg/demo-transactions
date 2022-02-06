package com.tomasguinzburg.demo.core.transactions;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.transactions.models.Transaction;

public interface TransactionCreator {
    Long create(Transaction transaction) throws ValidationException;
}
