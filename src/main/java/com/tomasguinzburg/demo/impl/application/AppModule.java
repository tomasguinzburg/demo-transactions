package com.tomasguinzburg.demo.impl.application;

import com.google.gson.*;
import com.tomasguinzburg.demo.core.accounts.AccountsService;
import com.tomasguinzburg.demo.core.accounts.impl.AccountsServiceImpl;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repository.AccountsRepository;
import com.tomasguinzburg.demo.core.repository.TransactionsRepository;
import com.tomasguinzburg.demo.core.transactions.TransactionsService;
import com.tomasguinzburg.demo.core.transactions.TransactionsServiceImpl;
import com.tomasguinzburg.demo.impl.repository.InMemoryAccountsRepositoryImpl;
import com.tomasguinzburg.demo.impl.repository.InMemoryTransactionsRepositoryImpl;
import com.tomasguinzburg.demo.impl.rest.GsonTransformer;
import com.tomasguinzburg.demo.impl.rest.NullPointerExceptionHandler;
import com.tomasguinzburg.demo.impl.rest.ValidationExceptionHandler;
import dagger.Module;
import dagger.Provides;
import spark.ResponseTransformer;

@Module
public class AppModule {

    //Toolkits
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }
    @Provides
    public ResponseTransformer provideResponseTransformer(Gson gson) {
        return new GsonTransformer(gson);
    }
    @Provides
    public ValidationExceptionHandler provideValidationExceptionHandler(){
        return new ValidationExceptionHandler(ValidationException.class);
    }
    @Provides
    public NullPointerExceptionHandler provideBNullPointerExceptionHandler(){
        return new NullPointerExceptionHandler(NullPointerException.class);
    }
    //Services
    @Provides
    public TransactionsService provideTransactionsService(TransactionsRepository repository, AccountsService accountsService) {
        return new TransactionsServiceImpl(repository, accountsService);
    }
    @Provides
    public AccountsService provideAccountService(AccountsRepository repository) {
        return new AccountsServiceImpl(repository);
    }

    //Repositories
    @Provides
    TransactionsRepository provideTransactionsRepository() {
        int CAPACITY = 100000000;
        return new InMemoryTransactionsRepositoryImpl(CAPACITY);
    }
    @Provides
    AccountsRepository provideAccountsRepository(){
        int CAPACITY = 10000;
        return new InMemoryAccountsRepositoryImpl(CAPACITY);
    }
}
