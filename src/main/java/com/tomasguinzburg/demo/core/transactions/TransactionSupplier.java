package com.tomasguinzburg.demo.core.transactions;

import com.tomasguinzburg.demo.core.transactions.models.Transaction;

public interface TransactionSupplier {
    Transaction get(Long ID);
}
