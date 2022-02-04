package com.tomasguinzburg.demo.transactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import spark.ResponseTransformer;

@Module
public class AppModule {

    @Provides
    public Gson provideGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Provides
    public ResponseTransformer provideResponseTransformer(Gson gson){

        return new GsonTransformer(gson);
    }
}
