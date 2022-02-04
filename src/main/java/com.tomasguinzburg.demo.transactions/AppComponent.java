package com.tomasguinzburg.demo.transactions;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    Router router();
}
