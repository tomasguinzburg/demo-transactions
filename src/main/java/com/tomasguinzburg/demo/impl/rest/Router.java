package com.tomasguinzburg.demo.impl.rest;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.impl.rest.endpoints.StateEndpoint;
import com.tomasguinzburg.demo.impl.rest.endpoints.TransactionsEndpoint;
import com.tomasguinzburg.demo.impl.rest.exception.handlers.NullPointerExceptionHandler;
import com.tomasguinzburg.demo.impl.rest.exception.handlers.ValidationExceptionHandler;
import spark.ResponseTransformer;

import javax.inject.Inject;
import javax.inject.Singleton;

import static spark.Spark.*;

@Singleton
public class Router {

    ResponseTransformer transformer;
    TransactionsEndpoint transactionsEndpoint;
    StateEndpoint stateEndpoint;
    ValidationExceptionHandler validationExceptionHandler;
    NullPointerExceptionHandler nullPointerExceptionHandler;

    @Inject
    Router( ResponseTransformer transformer
          , TransactionsEndpoint transactionsEndpoint
          , StateEndpoint stateEndpoint
          , ValidationExceptionHandler validationExceptionHandler
          , NullPointerExceptionHandler nullPointerExceptionHandler
          ) {
        this.transformer = transformer;
        this.transactionsEndpoint = transactionsEndpoint;
        this.stateEndpoint = stateEndpoint;
        this.validationExceptionHandler = validationExceptionHandler;
        this.nullPointerExceptionHandler = nullPointerExceptionHandler;
    }

    public void registerRoutes(){
        get("/ping", (req, res) -> "pong");

        post("/transactions", "application/json", transactionsEndpoint::createTransaction, transformer);
        get("/transactions", "application/json", transactionsEndpoint::getAllTransactions, transformer);
        get("transactions/status", "application/json", stateEndpoint::getState, transformer);
        get("/transactions/:id", "application/json", transactionsEndpoint::getTransaction, transformer);


        after((req, res) -> res.type("application/json"));
        exception(ValidationException.class, validationExceptionHandler);
        exception(NullPointerException.class, nullPointerExceptionHandler);
    }

}
