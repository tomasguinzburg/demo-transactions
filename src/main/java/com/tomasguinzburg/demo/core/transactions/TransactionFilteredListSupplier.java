package com.tomasguinzburg.demo.core.transactions;

import com.tomasguinzburg.demo.core.transactions.models.Transaction;
import com.tomasguinzburg.demo.core.transactions.models.TransactionQuery;

import java.util.List;

public interface TransactionFilteredListSupplier { List<Transaction> getByQuery(TransactionQuery filter); }
