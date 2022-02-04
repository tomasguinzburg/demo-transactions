package com.tomasguinzburg.demo.transactions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionsEndpoint {
    private TransactionsRepository transactionsRepository;
    private Gson gson;

    @Inject
    public TransactionsEndpoint(TransactionsRepository transactionsRepository, Gson gson) {
        this.transactionsRepository = transactionsRepository;
        this.gson = gson;
    }

    public Long createTransaction(Request request, Response response) {
        JsonObject requestDTO = gson.fromJson(request.body(),JsonObject.class);
        Transaction transaction = Transaction.builder()
                                             .accountIban(requestDTO.get("account_iban").getAsString())
                                             .amount(requestDTO.get("amount").getAsBigDecimal())
                                             .date(requestDTO.get("date").getAsString())
                                             .description(requestDTO.get("description").getAsString())
                                             .fee(requestDTO.get("fee").getAsBigDecimal())
                                             .reference(requestDTO.get("reference").getAsString())
                                             .build();

        return transactionsRepository.save(transaction);
    }

    public Transaction getTransaction(Request request, Response response) {
        Long ID = Long.valueOf(request.params(":id"));
        TransactionEntity entity = transactionsRepository.getByID(ID);
        return Transaction.builder()
                          .reference(entity.getReference())
                          .accountIban(entity.getAccountIban())
                          .amount(entity.getAmount())
                          .fee(entity.getFee())
                          .date(entity.getDate())
                          .description(entity.getDescription())
                          .build();
    }

    //TODO: it's definitely better to just build or import an object mapper, but meanwhile we just use a lambda
    public List<Transaction> getAllTransactions(Request request, Response response) {
        List<TransactionEntity> entities = transactionsRepository.getAll();
        return entities.stream()
                       .map(e -> Transaction.builder()
                                            .reference(e.getReference())
                                            .accountIban(e.getAccountIban())
                                            .date(e.getDate())
                                            .amount(e.getAmount())
                                            .fee(e.getFee())
                                            .description(e.getDescription())
                                            .build())
                      .collect(Collectors.toList());
    }
}
