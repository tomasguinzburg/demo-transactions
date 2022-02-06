package com.tomasguinzburg.demo.impl.application;

import com.tomasguinzburg.demo.impl.rest.Router;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    Router router();
}
