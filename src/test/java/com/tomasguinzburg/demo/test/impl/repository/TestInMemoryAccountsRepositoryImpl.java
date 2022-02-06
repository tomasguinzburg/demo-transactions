package com.tomasguinzburg.demo.test.impl.repository;

import com.tomasguinzburg.demo.core.accounts.Account;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.impl.repository.InMemoryAccountsRepositoryImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TestInMemoryAccountsRepositoryImpl {

    private InMemoryAccountsRepositoryImpl inMemoryAccountsRepository;
    private static final String EXISTING_IBAN = "ES9820385778983000760236";

    @Before
    public void setUp() {
        inMemoryAccountsRepository = new InMemoryAccountsRepositoryImpl(100);
    }

    @Test
    public void testGetAccountByID() {
        Account account = inMemoryAccountsRepository.get(1);
        assertEquals(1L, account.getOwnerUserID());
    }

    @Test
    public void testGetAccountByIBAN() {
        try {
            Account account = inMemoryAccountsRepository.get(EXISTING_IBAN);
            assertEquals(1L, account.getOwnerUserID());
        } catch(ValidationException e){
            fail();
        }
    }

    @Test
    public void testGetAll() {
        assertEquals(2, inMemoryAccountsRepository.getAll().size());
    }

    @Test
    public void updateBalanceByID() {
        Account account = inMemoryAccountsRepository.updateBalance(1L, BigDecimal.valueOf(100L));
        assertEquals(BigDecimal.valueOf(100L), account.getBalance());
    }

    @Test
    public void updateBalanceByIBAN() {
        try {
            Account account = inMemoryAccountsRepository.updateBalance(EXISTING_IBAN, BigDecimal.valueOf(200L));
            assertEquals(BigDecimal.valueOf(200L), account.getBalance());
        } catch (ValidationException e) {
            fail();
        }
    }

    @Test
    public void getAccountByIBANFail() {
        try {
            Account account = inMemoryAccountsRepository.get("Some fake IBAN");
            fail();
        } catch(ValidationException e){
            assertEquals("VAL002", e.getCode());
        }
    }

    @Test
    public void getAccountByIBANFail2() {
        try {
            Account repeated = Account.builder()
                                      .ownerUserID(1L)
                                      .iban("ES9820385778983000760236")
                                      .balance(new BigDecimal(10000000))
                                      .label("Repeated")
                                      .build();
            inMemoryAccountsRepository.save(repeated);
            Account account = inMemoryAccountsRepository.get(EXISTING_IBAN);
            fail();
        } catch(ValidationException e){
            assertEquals("VAL003", e.getCode());
        }
    }

    @Test
    public void updateAccountByIBANFail() {
        try {
            Account account = inMemoryAccountsRepository.updateBalance("Some fake IBAN", BigDecimal.valueOf(300L));
            fail();
        } catch(ValidationException e){
            assertEquals("VAL002", e.getCode());
        }
    }

    @Test
    public void updateAccountByIBANFail2() {
        try {
            Account repeated = Account.builder()
                    .ownerUserID(1L)
                    .iban("ES9820385778983000760236")
                    .balance(new BigDecimal(10000000))
                    .label("Repeated")
                    .build();
            inMemoryAccountsRepository.save(repeated);
            Account account = inMemoryAccountsRepository.updateBalance(EXISTING_IBAN, BigDecimal.valueOf(300L));
            fail();
        } catch(ValidationException e){
            assertEquals("VAL003", e.getCode());
        }
    }

}
