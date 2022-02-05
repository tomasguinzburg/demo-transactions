package com.tomasguinzburg.demo.impl.application;

public class App {

    private AppComponent appComponent;

    /**
     * Kick off the app through dependency injection
     */
    private void start() {
        appComponent = DaggerAppComponent.create();
        appComponent.router().registerRoutes();
    }

    public static void main(String[] args) {
        new App().start();
    }
}
