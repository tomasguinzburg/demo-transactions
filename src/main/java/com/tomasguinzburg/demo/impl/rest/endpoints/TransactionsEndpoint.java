package com.tomasguinzburg.demo.impl.rest.endpoints;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.transactions.Transaction;
import com.tomasguinzburg.demo.core.transactions.TransactionQuery;
import com.tomasguinzburg.demo.core.transactions.TransactionsService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.List;

public class TransactionsEndpoint {
    private final TransactionsService transactionsService;
    private final Gson gson;

    @Inject
    public TransactionsEndpoint(TransactionsService transactionsService, Gson gson) {
        this.transactionsService = transactionsService;
        this.gson = gson;
    }

    public Long createTransaction(Request request, Response response) throws ValidationException {
        Transaction transaction = gson.fromJson(request.body(), Transaction.class);
        return transactionsService.create(transaction);
    }

    public Transaction getTransaction(Request request, Response response) {
        Long ID = Long.valueOf(request.params(":id"));
        return transactionsService.get(ID);
    }


    public List<Transaction> getAllTransactions(Request request, Response response) {
        if (request.queryParams().contains("account_iban") || request.queryParams().contains("sorting"))
            return this.getTransactionsByFilter( request.queryParamOrDefault("account_iban", "")
                                               , request.queryParamOrDefault("sorting", "")
                                               );
        return transactionsService.getAll();
    }

    private List<Transaction> getTransactionsByFilter(String accountIban, String sorting) {
        TransactionQuery query = new TransactionQuery();
        query.setAccountIban(accountIban);
        query.setSorting(sorting);
        return transactionsService.getByQuery(query);

    }
}
