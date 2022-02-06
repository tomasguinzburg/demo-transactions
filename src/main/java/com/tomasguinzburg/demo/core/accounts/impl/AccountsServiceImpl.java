package com.tomasguinzburg.demo.core.accounts.impl;

import com.tomasguinzburg.demo.core.accounts.Account;
import com.tomasguinzburg.demo.core.accounts.AccountsService;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repository.AccountsRepository;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class AccountsServiceImpl implements AccountsService {

    AccountsRepository repository;

    @Inject
    public AccountsServiceImpl(AccountsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addToBalance(Long ID, BigDecimal amount) throws ValidationException {
        Account account = repository.get(ID);
        if (account.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("VAL001", "Insufficient funds.");
        }
        //TODO log this, someone's money is changing
        repository.updateBalance(ID, account.getBalance().add(amount));
    }

    @Override
    public void addToBalance(String iban, BigDecimal amount) throws ValidationException {
        Account account = repository.get(iban);
        if (account.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
            //TODO log this, someone's money is changing
            throw new ValidationException("VAL001", "Insufficient funds.");
        }
        //TODO log this, someone's money is changing
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
