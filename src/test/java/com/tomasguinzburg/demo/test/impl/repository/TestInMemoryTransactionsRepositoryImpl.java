package com.tomasguinzburg.demo.test.impl.repository;

import com.tomasguinzburg.demo.core.transactions.Transaction;
import com.tomasguinzburg.demo.impl.repository.InMemoryTransactionsRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;

public class TestInMemoryTransactionsRepositoryImpl {

    private InMemoryTransactionsRepositoryImpl inMemoryTransactionsRepository;
    private static final String EXISTING_IBAN = "ES9820385778983000760236";

    @Before
    public void setUp() {
        inMemoryTransactionsRepository = new InMemoryTransactionsRepositoryImpl(100);
    }

    @Test
    public void testSaveTransaction() {
        Transaction transaction = Transaction.builder()
                                             .amount(BigDecimal.valueOf(100L))
                                             .accountIban(EXISTING_IBAN)
                                             .date("2022-02-06T00:15:42.000Z")
                                             .description("Some random payment")
                                             .fee(BigDecimal.valueOf(3.18))
                                             .reference("f768aed6-25ca-4af1-bf78-bef3e26e3689")
                                             .build();
        long ID = inMemoryTransactionsRepository.save(transaction);
        assertEquals(1L, ID);
    }

    //Couldn't find a way to not test the save method at the same time, so I guess we depend on it from now on.
    @Test
    public void testGetAllTransactions() {
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(100L))
                .accountIban(EXISTING_IBAN)
                .date("2022-02-06T00:15:42.000Z")
                .description("Some random payment2")
                .fee(BigDecimal.valueOf(3.18))
                .reference("f768aed6-25ca-4af1-bf78-bef3e26e3680")
                .build();
        Transaction transaction2 = Transaction.builder()
                .amount(BigDecimal.valueOf(100L))
                .accountIban(EXISTING_IBAN)
                .date("2022-02-06T00:15:42.000Z")
                .description("Some random payment2")
                .fee(BigDecimal.valueOf(3.18))
                .reference("f768aed6-25ca-4af1-bf78-bef3e26e3680")
                .build();
        inMemoryTransactionsRepository.save(transaction);
        inMemoryTransactionsRepository.save(transaction2);
        assertEquals(2, inMemoryTransactionsRepository.getAll().size());
    }

    @Test
    public void testGetTransactionByID() {
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(100L))
                .accountIban(EXISTING_IBAN)
                .date("2022-02-06T00:15:42.000Z")
                .description("Some random payment2")
                .fee(BigDecimal.valueOf(3.18))
                .reference("f768aed6-25ca-4af1-bf78-bef3e26e3680")
                .build();
        Transaction transaction2 = Transaction.builder()
                .amount(BigDecimal.valueOf(100L))
                .accountIban(EXISTING_IBAN)
                .date("2022-02-06T00:15:42.000Z")
                .description("Some random payment3")
                .fee(BigDecimal.valueOf(3.18))
                .reference("f768aed6-25ca-4af1-bf78-bef3e26e3680")
                .build();
        inMemoryTransactionsRepository.save(transaction);
        inMemoryTransactionsRepository.save(transaction2);
        Transaction result = inMemoryTransactionsRepository.get(2);
        assertEquals("Some random payment3", result.getDescription());
    }




}
