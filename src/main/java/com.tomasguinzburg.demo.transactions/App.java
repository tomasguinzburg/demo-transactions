package com.tomasguinzburg.demo.transactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.*;

public class App {

    private AppComponent appComponent;

    /**
     * Kick off the app through dependency injection
     */
    private void start() {
        initializeDagger();
        registerRoutes();
    }

    private void initializeDagger() {
        appComponent = DaggerAppComponent.create();
    }

    private void registerRoutes() {
        appComponent.router().registerRoutes();
    }


    public static void main(String[] args) {

        new App().start();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonTransformer transformer = new GsonTransformer(gson);
        TransactionsRepository transactionsRepository = new TransactionsRepository();
        TransactionsEndpoint transactionsEndpoint = new TransactionsEndpoint(transactionsRepository, gson);

        get("/ping", (req, res) -> "pong");

        get("/transactions", "application/json", transactionsEndpoint::getAllTransactions, transformer);
        post("/transactions", "application/json", transactionsEndpoint::createTransaction, transformer);
        get("/transactions/:id", "application/json", transactionsEndpoint::getTransaction, transformer);

    }
}
