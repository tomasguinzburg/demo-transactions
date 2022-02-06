package com.tomasguinzburg.demo.core.repositories;

import com.tomasguinzburg.demo.core.accounts.models.Account;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository {
    /**
     * Gets the account by ID
     *
     * @param ID the account ID
     * @return the corresponding Account
     */
    Account get(long ID);
    /**
     * Gets the account by iban
     *
     * @param iban the account iban
     * @return the corresponding Account
     */
    Account get(String iban) throws ValidationException;

    /**
     * Gets all accounts
     *
     * @return all accounts in the system
     */
    List<Account> getAll();

    /**
     * Updates only the balance of an account.
     *
     * @apiNote  Exposing a fully fledged update method on an account repo is an easy way to allow a mistake
     * to cost a lot of money
     * @param ID        the account's ID
     * @param balance   the account's new balance
     * @return the updated account
     */
    Account updateBalance(Long ID, BigDecimal balance);
    /**
     * Updates only the balance of an account.
     *
     * @apiNote  Exposing a fully fledged update method on an account repo is an easy way to allow a mistake
     * to cost a lot of money
     * @param iban      the account's iban
     * @param balance   the account's new balance
     * @return the updated account
     */
    Account updateBalance(String iban, BigDecimal balance) throws ValidationException;

    /**
     * Saves a new account, if IBAN is unique
     *
     * @param account the account to create
     * @return the account's ID
     */
    Long save(Account account);

}
