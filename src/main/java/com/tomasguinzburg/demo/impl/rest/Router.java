package com.tomasguinzburg.demo.impl.rest;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.impl.rest.endpoints.TransactionsEndpoint;
import spark.ResponseTransformer;

import javax.inject.Inject;
import javax.inject.Singleton;

import static spark.Spark.*;

@Singleton
public class Router {

    ResponseTransformer transformer;
    TransactionsEndpoint transactionsEndpoint;
    ValidationExceptionHandler validationExceptionHandler;

    @Inject
    Router(ResponseTransformer transformer, TransactionsEndpoint transactionsEndpoint, ValidationExceptionHandler validationExceptionHandler) {
        this.transformer = transformer;
        this.transactionsEndpoint = transactionsEndpoint;
        this.validationExceptionHandler = validationExceptionHandler;
    }

    public void registerRoutes(){
        get("/ping", (req, res) -> "pong");

        post("/transactions", "application/json", transactionsEndpoint::createTransaction, transformer);
        get("/transactions", "application/json", transactionsEndpoint::getAllTransactions, transformer);
        get("/transactions/:id", "application/json", transactionsEndpoint::getTransaction, transformer);
        after((req, res) -> res.type("application/json"));
        exception(ValidationException.class, validationExceptionHandler);
    }

}
