package com.tomasguinzburg.demo.impl.rest.endpoints;

import com.google.gson.Gson;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.transactions.TransactionCreator;
import com.tomasguinzburg.demo.core.transactions.TransactionFilteredListSupplier;
import com.tomasguinzburg.demo.core.transactions.TransactionListSupplier;
import com.tomasguinzburg.demo.core.transactions.TransactionSupplier;
import com.tomasguinzburg.demo.core.transactions.impl.TransactionServiceImpl;
import com.tomasguinzburg.demo.core.transactions.models.Transaction;
import com.tomasguinzburg.demo.core.transactions.models.TransactionQuery;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.List;

public class TransactionsEndpoint {
    private final TransactionSupplier transactionSupplier;
    private final TransactionListSupplier transactionListSupplier;
    private final TransactionFilteredListSupplier transactionFilteredListSupplier;
    private final TransactionCreator transactionCreator;

    private final Gson gson;

    @Inject
    public TransactionsEndpoint( TransactionSupplier transactionSupplier
                               , TransactionListSupplier transactionListSupplier
                               , TransactionFilteredListSupplier transactionFilteredListSupplier
                               , TransactionCreator transactionCreator
                               , Gson gson
                               ) {
        this.transactionSupplier = transactionSupplier;
        this.transactionListSupplier = transactionListSupplier;
        this.transactionFilteredListSupplier = transactionFilteredListSupplier;
        this.transactionCreator = transactionCreator;
        this.gson = gson;
    }

    public Long createTransaction(Request request, Response response) throws ValidationException {
        return transactionCreator.create(gson.fromJson(request.body(), Transaction.class));
    }

    public Transaction getTransaction(Request request, Response response) {
        return transactionSupplier.get(Long.valueOf(request.params(":id")));
    }


    public List<Transaction> getAllTransactions(Request request, Response response) {
        if (request.queryParams().contains("account_iban") || request.queryParams().contains("sorting"))
            return this.getTransactionsByFilter( request.queryParamOrDefault("account_iban", "")
                                               , request.queryParamOrDefault("sorting", "")
                                               );
        return transactionListSupplier.getAll();
    }

    private List<Transaction> getTransactionsByFilter(String accountIban, String sorting) {
        return transactionFilteredListSupplier.getByQuery(TransactionQuery.builder()
                                                                          .accountIban(accountIban)
                                                                          .sorting(sorting)
                                                                          .build());

    }
}
