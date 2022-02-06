package com.tomasguinzburg.demo.core.accounts;

import com.tomasguinzburg.demo.core.accounts.models.Account;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;

public interface AccountSupplierByIBAN { Account get(String iban) throws ValidationException; }
