package com.tomasguinzburg.demo.transactions;

import spark.ResponseTransformer;

import javax.inject.Inject;
import javax.inject.Singleton;

import static spark.Spark.*;

@Singleton
public class Router {

    ResponseTransformer transformer;
    TransactionsEndpoint transactionsEndpoint;

    @Inject
    Router(ResponseTransformer transformer, TransactionsEndpoint transactionsEndpoint) {
        this.transformer = transformer;
        this.transactionsEndpoint = transactionsEndpoint;
    }

    public void registerRoutes(){
        get("/ping", (req, res) -> "pong");

        get("/transactions", "application/json", transactionsEndpoint::getAllTransactions, transformer);
        post("/transactions", "application/json", transactionsEndpoint::createTransaction, transformer);
        get("/transactions/:id", "application/json", transactionsEndpoint::getTransaction, transformer);

    }

}
