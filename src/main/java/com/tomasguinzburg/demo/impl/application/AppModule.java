package com.tomasguinzburg.demo.impl.application;

import com.google.gson.*;
import com.tomasguinzburg.demo.core.accounts.AccountBalanceAdderByIBAN;
import com.tomasguinzburg.demo.core.accounts.AccountSupplierByIBAN;
import com.tomasguinzburg.demo.core.accounts.impl.AccountServiceImpl;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repositories.AccountRepository;
import com.tomasguinzburg.demo.core.repositories.TransactionRepository;
import com.tomasguinzburg.demo.core.state.StateSupplier;
import com.tomasguinzburg.demo.core.state.impl.StateServiceImpl;
import com.tomasguinzburg.demo.core.state.models.State;
import com.tomasguinzburg.demo.core.transactions.TransactionCreator;
import com.tomasguinzburg.demo.core.transactions.TransactionFilteredListSupplier;
import com.tomasguinzburg.demo.core.transactions.TransactionListSupplier;
import com.tomasguinzburg.demo.core.transactions.TransactionSupplier;
import com.tomasguinzburg.demo.core.transactions.impl.TransactionServiceImpl;
import com.tomasguinzburg.demo.core.transactions.models.Transaction;
import com.tomasguinzburg.demo.impl.repositories.InMemoryAccountRepositoryImpl;
import com.tomasguinzburg.demo.impl.repositories.InMemoryTransactionRepositoryImpl;
import com.tomasguinzburg.demo.impl.rest.GsonTransformer;
import com.tomasguinzburg.demo.impl.rest.exception.handlers.NullPointerExceptionHandler;
import com.tomasguinzburg.demo.impl.rest.exception.handlers.ValidationExceptionHandler;
import dagger.Module;
import dagger.Provides;
import spark.ResponseTransformer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Module
public class AppModule {

    //Toolkits
    @Provides
    Gson provideGson() {
        return new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }
    @Provides
    ResponseTransformer provideResponseTransformer(Gson gson) {
        return new GsonTransformer(gson);
    }

    // Exception Handlers
    @Provides
    ValidationExceptionHandler provideValidationExceptionHandler() {
        return new ValidationExceptionHandler(ValidationException.class);
    }
    @Provides
    NullPointerExceptionHandler provideBNullPointerExceptionHandler() {
        return new NullPointerExceptionHandler(NullPointerException.class);
    }

    // Services
    //Transaction Service
    @Provides TransactionCreator provideTransactionCreator(TransactionServiceImpl impl) { return impl; }
    @Provides TransactionFilteredListSupplier provideTransactionFilteredListSupplier(TransactionServiceImpl impl) { return impl; }
    @Provides TransactionListSupplier provideTransactionListSupplier(TransactionServiceImpl impl) { return impl; }
    @Provides TransactionSupplier provideTransactionSupplier( TransactionServiceImpl impl ) { return impl; }
    @Provides @Singleton
    TransactionServiceImpl provideTransactionServiceImpl( TransactionRepository repository
                                                               , AccountBalanceAdderByIBAN accountBalanceAdderByIBAN
                                                               , AccountSupplierByIBAN accountSupplierByIBAN
                                                               ) {
        return new TransactionServiceImpl(repository, accountBalanceAdderByIBAN, accountSupplierByIBAN);
    }

    //Account Service
    @Provides AccountBalanceAdderByIBAN provideAccountBalanceAdderByIBAN(AccountServiceImpl impl) { return impl; }
    @Provides AccountSupplierByIBAN provideAccountSupplierByIBAN(AccountServiceImpl impl) { return impl; }
    @Provides @Singleton
    AccountServiceImpl provideAccountServiceImpl(AccountRepository repository) {
        return new AccountServiceImpl(repository);
    }

    //State service
    @Provides StateSupplier provideStateSupplier(StateServiceImpl impl) { return impl; }
    @Provides @Singleton
    StateServiceImpl provideStateServiceImpl(TransactionRepository repository) {
        return new StateServiceImpl(repository);
    }

    //Repositories
    @Provides @Singleton
    TransactionRepository provideTransactionsRepository() {
        int CAPACITY = 1000;
        return new InMemoryTransactionRepositoryImpl(CAPACITY);
    }
    @Provides @Singleton
    AccountRepository provideAccountsRepository(){
        int CAPACITY = 10;
        return new InMemoryAccountRepositoryImpl(CAPACITY);
    }
}
