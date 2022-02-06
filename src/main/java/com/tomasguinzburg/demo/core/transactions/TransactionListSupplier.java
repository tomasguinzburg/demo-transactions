package com.tomasguinzburg.demo.core.transactions;

import com.tomasguinzburg.demo.core.transactions.models.Transaction;

import java.util.List;

public interface TransactionListSupplier { List<Transaction> getAll(); }
