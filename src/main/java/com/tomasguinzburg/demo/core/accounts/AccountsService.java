package com.tomasguinzburg.demo.core.accounts;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountsService {

    void addToBalance(Long ID, BigDecimal amount) throws ValidationException;
    void addToBalance(String iban, BigDecimal amount) throws ValidationException;
    Account get(Long ID);
    Account get(String iban) throws ValidationException;
    List<Account> getAll();

}
