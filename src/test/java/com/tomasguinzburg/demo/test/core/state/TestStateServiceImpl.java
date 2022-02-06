package com.tomasguinzburg.demo.test.core.state;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repositories.TransactionRepository;
import com.tomasguinzburg.demo.core.transactions.models.Transaction;
import com.tomasguinzburg.demo.core.state.models.State;
import com.tomasguinzburg.demo.core.state.models.StateQuery;
import com.tomasguinzburg.demo.core.state.StateSupplier;
import com.tomasguinzburg.demo.core.state.impl.StateServiceImpl;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestStateServiceImpl {

    StateSupplier stateService;

    private static final String ACCOUNT_IBAN = "ES9820385778983000760237";
    private static final String DATE0 = "2019-07-16T16:55:42.000Z";
    private static final String DATE1 = "2022-07-16T16:55:42.000Z";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final String DATE2 = sdf.format(new Date());
    private static final BigDecimal AMOUNT0 = BigDecimal.valueOf(100);
    private static final BigDecimal AMOUNT1 = BigDecimal.valueOf(-100);
    private static final BigDecimal AMOUNT2 = BigDecimal.valueOf(1000);
    private static final BigDecimal AMOUNT3 = BigDecimal.valueOf(300);
    private static final BigDecimal FEE0 = BigDecimal.valueOf(3.18);
    private static final BigDecimal FEE1 = BigDecimal.valueOf(-3.18);
    private static final BigDecimal FEE2 = BigDecimal.valueOf(0);
    private static final BigDecimal FEE3 = BigDecimal.valueOf(12.18);
    private static final String REFERENCE0 = "68d70bfe-d952-44f2-a007-ee2a65b985a4";
    private static final String REFERENCE1 = "68d70bfe-d952-44f2-a007-ee2a65b90000";
    private static final String REFERENCE2 = "68d70bfe-d952-44f2-a007-ee2a65b985a5";
    private static final String REFERENCE3 = "68d70bfe-d952-44f2-a007-ee2a65b985a6";
    private static final String BADREFERENCE = "PLEASE-DO-NOT-USE-THIS-REFERENCE";
    private final Transaction transaction0 = Transaction.builder()
                                                        .amount(AMOUNT0)
                                                        .accountIban(ACCOUNT_IBAN)
                                                        .date(DATE0)
                                                        .description("Past")
                                                        .fee(FEE0)
                                                        .reference(REFERENCE0)
                                                        .build();
    private final Transaction transaction1 = Transaction.builder()
                                                        .amount(AMOUNT1)
                                                        .accountIban(ACCOUNT_IBAN)
                                                        .date(DATE1)
                                                        .description("Future")
                                                        .fee(FEE1)
                                                        .reference(REFERENCE1)
                                                        .build();
    private final Transaction transaction2 = Transaction.builder()
                                                        .amount(AMOUNT2)
                                                        .accountIban(ACCOUNT_IBAN)
                                                        .date(DATE2)
                                                        .description("Today")
                                                        .fee(FEE2)
                                                        .reference(REFERENCE2)
                                                        .build();
    private final Transaction transaction3 = Transaction.builder()
                                                        .amount(AMOUNT3)
                                                        .accountIban(ACCOUNT_IBAN)
                                                        .description("No date")
                                                        .fee(FEE3)
                                                        .reference(REFERENCE3)
                                                        .build();

    @Before
    public void setUp(){

        List<Transaction> transactions = new ArrayList<>(Arrays.asList(transaction0, transaction1, transaction2, transaction3));
        TransactionRepository mockTransactionRepository = mock(TransactionRepository.class);
        when(mockTransactionRepository.getAll()).thenReturn(transactions);
        when(mockTransactionRepository.get(transaction0.getReference())).thenReturn(transaction0);
        when(mockTransactionRepository.get(transaction1.getReference())).thenReturn(transaction1);
        when(mockTransactionRepository.get(transaction2.getReference())).thenReturn(transaction2);
        when(mockTransactionRepository.get(transaction3.getReference())).thenReturn(transaction3);
        doThrow(NoSuchElementException.class).when(mockTransactionRepository).get(BADREFERENCE);

        stateService = new StateServiceImpl(mockTransactionRepository);
    }

    @Test
    //Test business rule a: transaction not present
    //Given: A transaction that is not stored in our system
    //When: I check the status from any channel
    //Then: The system returns the status 'INVALID'
    public void testA() throws ValidationException {
        StateQuery query = StateQuery.builder()
                .reference("PLEASE-DO-NOT-USE-THIS-REFERENCE")
                .channel("CLIENT")
                .build();

        State state = stateService.get(query);
        assertEquals("INVALID", state.getStatus());
        assertEquals("PLEASE-DO-NOT-USE-THIS-REFERENCE", state.getReference());
        assertNull(state.getAmount());
        assertNull(state.getFee());
    }

    @Test
    //Test business rule b:
    //Given: A transaction that is stored in our system
    //When: I check the status from CLIENT or ATM channel
    //And the transaction date is before today
    //Then: The system returns the status 'SETTLED'
    //And the amount subtracting the fee
    public void testB() throws ValidationException {
        StateQuery query0 = StateQuery.builder()
                .reference(REFERENCE0)
                .channel("CLIENT")
                .build();
        StateQuery query1 = StateQuery.builder()
                .reference(REFERENCE0)
                .channel("ATM")
                .build();

        State state0 = stateService.get(query0);
        State state1 = stateService.get(query1);
        assertEquals("SETTLED", state0.getStatus());
        assertEquals("SETTLED", state1.getStatus());
        assertEquals(REFERENCE0, state0.getReference());
        assertEquals(REFERENCE0, state1.getReference());
        assertNull(state0.getFee());
        assertNull(state1.getFee());
        assertEquals(AMOUNT0.subtract(FEE0), state0.getAmount());
        assertEquals(AMOUNT0.subtract(FEE0), state1.getAmount());
    }

    @Test
    //Test business rule c:
    //Given: A transaction that is stored in our system
    //When: I check the status from INTERNAL channel
    //And the transaction date is before today
    //Then: The system returns the status 'SETTLED'
    //And the amount
    //And the fee
    public void testC() throws ValidationException {
        StateQuery query = StateQuery.builder()
                .reference(REFERENCE0)
                .channel("INTERNAL")
                .build();

        State state = stateService.get(query);
        assertEquals("SETTLED", state.getStatus());
        assertEquals(REFERENCE0, state.getReference());
        assertEquals(AMOUNT0, state.getAmount());
        assertEquals(FEE0, state.getFee());
    }

    @Test
    //Test business rule d:
    //Given: A transaction that is stored in our system
    //When: I check the status from CLIENT or ATM channel
    //And the transaction date is equals to today
    //Then: The system returns the status 'PENDING'
    //And the amount subtracting the fee
    // Also tests not receiving fee means subtracting 0
    public void testD() throws ValidationException {
        StateQuery query0 = StateQuery.builder()
                .reference(REFERENCE2)
                .channel("CLIENT")
                .build();
        StateQuery query1 = StateQuery.builder()
                .reference(REFERENCE2)
                .channel("ATM")
                .build();
        State state0 = stateService.get(query0);
        State state1 = stateService.get(query1);

        assertEquals("PENDING", state0.getStatus());
        assertEquals("PENDING", state1.getStatus());
        assertEquals(REFERENCE2, state0.getReference());
        assertEquals(REFERENCE2, state1.getReference());
        assertEquals(AMOUNT2, state0.getAmount());
        assertEquals(AMOUNT2, state1.getAmount());
        assertNull(state0.getFee());
        assertNull(state1.getFee());
    }

    @Test
    //Test business rule e:
    //Given: A transaction that is stored in our system
    //When: I check the status from INTERNAL channel
    //And the transaction date is equals to today
    //Then: The system returns the status 'PENDING'
    //And the amount
    //And the fee
    public void testE() throws ValidationException {
        StateQuery query = StateQuery.builder()
                .reference(REFERENCE2)
                .channel("INTERNAL")
                .build();

        State state = stateService.get(query);
        assertEquals("PENDING", state.getStatus());
        assertEquals(REFERENCE2, state.getReference());
        assertEquals(AMOUNT2, state.getAmount());
        assertEquals(FEE2, state.getFee());
    }

    @Test
    //Test business rule f:
    //Given: A transaction that is stored in our system
    //When: I check the status from CLIENT channel
    //And the transaction date is greater than today
    //Then: The system returns the status 'FUTURE'
    //And the amount subtracting the fee
    public void testF() throws ValidationException {
        StateQuery query = StateQuery.builder()
                .reference(REFERENCE1)
                .channel("CLIENT")
                .build();

        State state = stateService.get(query);
        assertEquals("FUTURE", state.getStatus());
        assertEquals(REFERENCE1, state.getReference());
        assertEquals(AMOUNT1.subtract(FEE1), state.getAmount());
        assertNull(state.getFee());
    }

    @Test
    //Test business rule g
    //Given: A transaction that is stored in our system
    //When: I check the status from ATM channel
    //And the transaction date is greater than today
    //Then: The system returns the status 'PENDING'
    //And the amount subtracting the fee
    // This one is really annoying
    public void testG() throws ValidationException {
        StateQuery query = StateQuery.builder()
                .reference(REFERENCE1)
                .channel("ATM")
                .build();

        State state = stateService.get(query);
        assertEquals("PENDING", state.getStatus());
        assertEquals(REFERENCE1, state.getReference());
        assertEquals(AMOUNT1.subtract(FEE1), state.getAmount());
        assertNull(state.getFee());
    }

    @Test
    //Test business rule h
    //Given: A transaction that is stored in our system
    //When: I check the status from INTERNAL channel
    //And the transaction date is greater than today
    //Then: The system returns the status 'FUTURE'
    //And the amount
    //And the fee
    public void testH() throws ValidationException {
        StateQuery query = StateQuery.builder()
                .reference(REFERENCE1)
                .channel("INTERNAL")
                .build();

        State state = stateService.get(query);
        assertEquals("FUTURE", state.getStatus());
        assertEquals(AMOUNT1, state.getAmount());
        assertEquals(REFERENCE1, state.getReference());
        assertEquals(FEE1, state.getFee());
    }

    @Test
    //Test querying is disallowed when channel is not sent
    // Even thought the field is optional and won't throw null pointer, giving transaction data to an unidentified
    // channel sounds like a bad thing.
    public void testChannelLess() {
        StateQuery query = StateQuery.builder()
                .reference(REFERENCE0)
                .build();
        try{
            stateService.get(query);
            fail();
        } catch(ValidationException e) {
            assertEquals("VAL006", e.getCode());
        }
    }
}
