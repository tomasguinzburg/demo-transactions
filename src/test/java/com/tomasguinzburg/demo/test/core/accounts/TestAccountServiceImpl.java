package com.tomasguinzburg.demo.test.core.accounts;

import com.tomasguinzburg.demo.core.accounts.impl.AccountServiceImpl;
import com.tomasguinzburg.demo.core.accounts.models.Account;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repositories.AccountRepository;
import com.tomasguinzburg.demo.impl.repositories.InMemoryAccountRepositoryImpl;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TestAccountServiceImpl {

    private AccountServiceImpl accountService;
    private static final String LABEL0 = "ACCOUNT0";
    private static final String IBAN0 = "ES9820385778983000760237";
    private static final BigDecimal BALANCE0 = BigDecimal.valueOf(10000L);
    private final Account account0 = Account.builder().ownerUserID(1).balance(BALANCE0).iban(IBAN0).label(LABEL0).build();
    private static final String LABEL1 = "ACCOUNT1";
    private static final String IBAN1 = "ES9820385778983000760236";
    private static final BigDecimal BALANCE1 = BigDecimal.valueOf(100L);
    private final Account account1 = Account.builder().ownerUserID(2).balance(BALANCE1).iban(IBAN1).label(LABEL1).build();

    @SneakyThrows
    @Before
    public void setUp() {
        AccountRepository accountRepository = mock(InMemoryAccountRepositoryImpl.class);
        when(accountRepository.getAll()).thenReturn(new ArrayList<>(Arrays.asList(account0, account1)));
        when(accountRepository.get(1L)).thenReturn(account0);
        when(accountRepository.get(2L)).thenReturn(account1);
        when(accountRepository.get(IBAN0)).thenReturn(account0);
        when(accountRepository.get(IBAN1)).thenReturn(account1);
        when(accountRepository.updateBalance(eq(IBAN0), any(BigDecimal.class))).thenReturn(
                Account.builder()
                       .ownerUserID(account0.getOwnerUserID())
                       .iban(IBAN0)
                       .label(LABEL0)
                       .balance(BALANCE0)
                       .build()
        );

        this.accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    public void testGetAccount() {
        Account account = this.accountService.get(1L);
        assertEquals(BALANCE0, account.getBalance());
    }

    @SneakyThrows
    @Test
    public void testGetAccountByIBAN() {
        Account account = this.accountService.get(IBAN0);
        assertEquals(LABEL0, account.getLabel());
    }

    @Test
    public void testGetAll() {
        List<Account> accounts = this.accountService.getAll();
        assertEquals(2, accounts.size());
    }

    @Test
    public void testAddToBalance() {
        try{
            this.accountService.addToBalance(IBAN0, BigDecimal.valueOf(10));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddToBalanceFails() {
        try {
            this.accountService.addToBalance(IBAN0, BigDecimal.valueOf(-10000000000000L));
            fail();
        } catch (ValidationException e) {
            assertEquals(e.getCode(), "VAL001");
        }
    }


}
