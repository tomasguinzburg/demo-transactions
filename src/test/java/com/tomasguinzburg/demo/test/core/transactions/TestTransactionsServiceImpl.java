package com.tomasguinzburg.demo.test.core.transactions;

import com.tomasguinzburg.demo.core.accounts.Account;
import com.tomasguinzburg.demo.core.accounts.AccountsService;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repository.TransactionsRepository;
import com.tomasguinzburg.demo.core.transactions.Transaction;
import com.tomasguinzburg.demo.core.transactions.TransactionQuery;
import com.tomasguinzburg.demo.core.transactions.TransactionsService;
import com.tomasguinzburg.demo.core.transactions.TransactionsServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestTransactionsServiceImpl {

    private TransactionsService transactionsService;

    private static final String ACCOUNT_IBAN = "ES9820385778983000760237";
    private static final String DATE = "2019-07-16T16:55:42.000Z";
    private static final int AMOUNT = 100;
    private static final int AMOUNT_2 = -1000;
    private static final int AMOUNT_3 = -30;
    private static final int BALANCE = 500;
    private static final long EXPECTED_ID = 3L;
    private static final String BAD_IBAN = "123";
    private static final String DUPLICATE_IBAN = "321";

    Transaction mockTransaction = Transaction.builder()
                                             .amount(new BigDecimal(AMOUNT))
                                             .accountIban(ACCOUNT_IBAN)
                                             .date(DATE)
                                             .description("Restaurant payment")
                                             .fee(new BigDecimal("3.50"))
                                             .reference("68d70bfe-d952-44f2-a007-ee2a65b985a4")
                                             .build();
    Transaction mockTransaction2 = Transaction.builder()
                                              .amount(new BigDecimal(AMOUNT_2))
                                              .accountIban(ACCOUNT_IBAN)
                                              .date(DATE)
                                              .description("Gas")
                                              .fee(new BigDecimal("3.50"))
                                              .reference("f768aed6-25ca-4af1-bf78-bef3e26e3689")
                                              .build();

    Transaction success = Transaction.builder()
                                     .amount(new BigDecimal(AMOUNT_3))
                                     .accountIban(ACCOUNT_IBAN)
                                     .date(DATE)
                                     .description("This one works")
                                     .fee(BigDecimal.valueOf(-3.45))
                                     .reference("123123123123")
                                     .build();

    Account mockAccount = Account.builder()
                                 .iban(ACCOUNT_IBAN)
                                 .balance(new BigDecimal(BALANCE))
                                 .label("TEST ACCOUNT")
                                 .ownerUserID(1L)
                                 .build();

    @Before
    public void setUp() throws ValidationException {

        TransactionsRepository mockRepository = mock(TransactionsRepository.class);
        when(mockRepository.get(1L)).thenReturn(mockTransaction);
        when(mockRepository.get(2L)).thenReturn(mockTransaction2);

        Transaction[] transactions = {mockTransaction, mockTransaction2};
        when(mockRepository.getAll()).thenReturn(new ArrayList<>(Arrays.asList(transactions)));

        when(mockRepository.save(any(Transaction.class))).thenReturn(4L);
        when(mockRepository.save(success)).thenReturn(3L);
        AccountsService mockAccountService = mock(AccountsService.class);
        when(mockAccountService.get(ACCOUNT_IBAN)).thenReturn(mockAccount);
        when(mockAccountService.get(BAD_IBAN)).thenThrow(new ValidationException("VAL002", "There are no accounts with this IBAN"));
        when(mockAccountService.get(DUPLICATE_IBAN)).thenThrow(new ValidationException("VAL003", "There are multiple accounts with the same IBAN. Call your bank"));

        transactionsService = new TransactionsServiceImpl(mockRepository, mockAccountService);
    }

    @Test
    public void testGet(){
        Transaction result = transactionsService.get(1L);
        assertEquals(ACCOUNT_IBAN, result.getAccountIban());
        assertEquals( new BigDecimal(AMOUNT), result.getAmount());
    }

    @Test
    public void testGetAll() {
        List<Transaction> result = transactionsService.getAll();
        assertEquals(2, result.size());
        assertEquals( new BigDecimal(AMOUNT), result.get(0).getAmount());
        assertEquals(new BigDecimal(AMOUNT_2), result.get(1).getAmount());
    }

    @Test
    public void testGetByQuery() {
        TransactionQuery query = new TransactionQuery();
        TransactionQuery badQuery = new TransactionQuery();
        query.setAccountIban(ACCOUNT_IBAN);
        query.setSorting("ascending");
        badQuery.setAccountIban("1234123");

        List<Transaction> result = transactionsService.getByQuery(query);
        List<Transaction> badResult = transactionsService.getByQuery(badQuery);
        query.setSorting("descending");
        List<Transaction> anotherGoodResult = transactionsService.getByQuery(query);

        assertEquals(new BigDecimal(AMOUNT_2), result.get(0).getAmount());
        assertEquals(0, badResult.size());
        assertEquals(new BigDecimal(AMOUNT), anotherGoodResult.get(0).getAmount());
    }

    @Test
    public void testCreateTransactionSuccess() {

        try {
            long ID = transactionsService.create(success);
            assertEquals(EXPECTED_ID, ID);
        } catch (ValidationException e) {
            fail();
        }
    }

    @Test
    public void testCreateTransactionSuccessEmptyFee(){
        Transaction emptyFee = Transaction.builder()
                                          .amount(new BigDecimal(AMOUNT_3))
                                          .accountIban(ACCOUNT_IBAN)
                                          .date(DATE)
                                          .description("This one works")
                                          .reference("123123123123")
                                          .build();
        try {
            long ID = transactionsService.create(emptyFee);
            assertEquals(4L, ID);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateTransactionAsignReferenceSuccess() {

        Transaction successCreateReference = Transaction.builder()
                                                        .amount(new BigDecimal(AMOUNT_3))
                                                        .accountIban(ACCOUNT_IBAN)
                                                        .date(DATE)
                                                        .description("This one works")
                                                        .fee(BigDecimal.valueOf(-3.45))
                                                        .reference("")
                                                        .build();
        try {
            long ID = transactionsService.create(successCreateReference);
            assertEquals(4L, ID);
        } catch(ValidationException e) {
            fail();
        }
    }

    @Test
    public void testCreateTransactionFailByFunds() {
        Transaction failByFunds = Transaction.builder()
                                             .amount(new BigDecimal(AMOUNT_2))
                                             .accountIban(ACCOUNT_IBAN)
                                             .date(DATE)
                                             .description("This one fails")
                                             .reference("123123123123")
                                             .build();
        try {
            transactionsService.create(failByFunds);
            fail();
        } catch (ValidationException e) {
            assertEquals("VAL001", e.code);     //Insufficient funds
        }
    }

    @Test
    public void testCreateTransactionFailByFundsNoFee(){
        Transaction failByFunds = Transaction.builder()
                .amount(new BigDecimal(AMOUNT_2))
                .accountIban(ACCOUNT_IBAN)
                .date(DATE)
                .description("This one fails")
                .fee(BigDecimal.valueOf(-3.45))
                .reference("123123123123")
                .build();
        try {
            transactionsService.create(failByFunds);
            fail();
        } catch (ValidationException e) {
            assertEquals("VAL001", e.code);     //Insufficient funds
        }
    }

    @Test
    public void testCreateTransactionFailByInexistentIBAN() {
        Transaction failByInexistentIBAN = Transaction.builder()
                .amount(BigDecimal.TEN)
                .accountIban(BAD_IBAN)
                .date(DATE)
                .description("This one fails")
                .fee(BigDecimal.valueOf(-3.45))
                .reference("123123123123")
                .build();
        try {
            transactionsService.create(failByInexistentIBAN);
            fail();
        } catch (ValidationException e) {
            assertEquals("VAL002", e.code);     //Inexistent IBAN
        }
    }

    @Test
    public void testCreateTransactionFailByDuplicateIBAN() {
        Transaction failByDuplicateIBAN = Transaction.builder()
                .amount(BigDecimal.TEN)
                .accountIban(DUPLICATE_IBAN)
                .date(DATE)
                .description("This one fails")
                .fee(BigDecimal.valueOf(-3.45))
                .reference("123123123123")
                .build();
        try {
            transactionsService.create(failByDuplicateIBAN);
            fail();
        } catch (ValidationException e) {
            assertEquals("VAL003", e.code);     //Duplicate IBAN
        }
    }

    @Test
    public void testCreateTransactionFailByReference() {
        Transaction failByReference = Transaction.builder()
                                                 .amount(new BigDecimal(AMOUNT_3))
                                                 .accountIban(ACCOUNT_IBAN)
                                                 .date(DATE)
                                                 .description("This one fails")
                                                 .fee(BigDecimal.valueOf(-3.45))
                                                 .reference("f768aed6-25ca-4af1-bf78-bef3e26e3689")
                                                 .build();
        try {
            transactionsService.create(failByReference);
            fail();
        } catch (ValidationException e) {
            assertEquals("VAL004", e.code);     //Reference in use
        }
    }

    @Test
    public void tesCreateTransactionFailByAmountZero() {
        Transaction failByAmountZero = Transaction.builder()
                                                  .amount(BigDecimal.ZERO)
                                                  .accountIban(ACCOUNT_IBAN)
                                                  .date(DATE)
                                                  .description("This one fails")
                                                  .fee(BigDecimal.valueOf(-3.45))
                                                  .reference("123123123123")
                                                  .build();
        try {
            transactionsService.create(failByAmountZero);
            fail();
        } catch (ValidationException e) {
            assertEquals("VAL005", e.code);     //Invalid amount
        }
    }


}
