package com.tomasguinzburg.demo.core.accounts;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;

import java.math.BigDecimal;

public interface AccountBalanceAdderByIBAN {

    void addToBalance(String iban, BigDecimal amount) throws ValidationException;


}
