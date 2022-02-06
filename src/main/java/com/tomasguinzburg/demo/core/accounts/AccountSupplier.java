package com.tomasguinzburg.demo.core.accounts;

import com.tomasguinzburg.demo.core.accounts.models.Account;

public interface AccountSupplier { Account get(Long ID); }
