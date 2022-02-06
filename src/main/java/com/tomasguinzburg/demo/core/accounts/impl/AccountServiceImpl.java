package com.tomasguinzburg.demo.core.accounts.impl;

import com.tomasguinzburg.demo.core.accounts.AccountBalanceAdderByIBAN;
import com.tomasguinzburg.demo.core.accounts.AccountSupplier;
import com.tomasguinzburg.demo.core.accounts.AccountSupplierByIBAN;
import com.tomasguinzburg.demo.core.accounts.models.Account;
import com.tomasguinzburg.demo.core.accounts.AccountListSupplier;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repositories.AccountRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

@Singleton
public class AccountServiceImpl implements AccountBalanceAdderByIBAN
                                          , AccountSupplier
                                          , AccountSupplierByIBAN
                                          , AccountListSupplier {

    AccountRepository repository;

    @Inject
    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addToBalance(String iban, BigDecimal amount) throws ValidationException {
        Account account = repository.get(iban);
        if (account.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("VAL001", "Insufficient funds.");
        }
        repository.updateBalance(iban, account.getBalance().add(amount));
    }

    @Override
    public Account get(Long ID) {
        return this.repository.get(ID);
    }

    @Override
    public Account get(String iban) throws ValidationException {
        return this.repository.get(iban);
    }

    @Override
    public List<Account> getAll() {
        return this.repository.getAll();
    }
}
