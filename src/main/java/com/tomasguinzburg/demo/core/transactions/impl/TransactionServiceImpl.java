package com.tomasguinzburg.demo.core.transactions.impl;

import com.tomasguinzburg.demo.core.accounts.AccountBalanceAdderByIBAN;
import com.tomasguinzburg.demo.core.accounts.AccountSupplierByIBAN;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repositories.TransactionRepository;
import com.tomasguinzburg.demo.core.transactions.*;
import com.tomasguinzburg.demo.core.transactions.models.Transaction;
import com.tomasguinzburg.demo.core.transactions.models.TransactionQuery;
import lombok.NonNull;
import spark.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class TransactionServiceImpl implements TransactionFilteredListSupplier, TransactionCreator, TransactionSupplier, TransactionListSupplier {

    private final TransactionRepository repository;
    private final AccountBalanceAdderByIBAN accountBalanceAdderByIBAN;
    private final AccountSupplierByIBAN accountSupplierByIBAN;

    @Inject
    public TransactionServiceImpl( TransactionRepository repository
                                 , AccountBalanceAdderByIBAN accountBalanceAdderByIBAN
                                 , AccountSupplierByIBAN accountSupplierByIBAN
                                 ){
        this.repository = repository;
        this.accountBalanceAdderByIBAN = accountBalanceAdderByIBAN;
        this.accountSupplierByIBAN = accountSupplierByIBAN;
    }

    @Override
    public Long create(final Transaction transaction_prototype) throws ValidationException {
        Transaction transaction = fillMissingValues(transaction_prototype);
        validateTransaction(transaction);
        processTransaction(transaction);
        return repository.save(transaction);
    }

    @Override
    public Transaction get(Long i) {
        return repository.get(i);
    }

    @Override
    public List<Transaction> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Transaction> getByQuery(@NonNull TransactionQuery query) {
        Stream<Transaction> stream = this.getAll().stream();

        if (StringUtils.isNotBlank(query.getAccountIban()))
            stream = stream.filter(t -> t.getAccountIban().equals(query.getAccountIban()));

        if (StringUtils.isNotBlank(query.getSorting()) && query.getSorting().equals("ascending"))
            stream = stream.sorted(Comparator.comparing(Transaction::getAmount));
        else if (StringUtils.isNotBlank(query.getSorting()) && query.getSorting().equals("descending"))
            stream = stream.sorted(Comparator.comparing(Transaction::getAmount).reversed());

        return stream.collect(Collectors.toList());
    }


    private void processTransaction(@NonNull Transaction transaction) throws ValidationException {
        BigDecimal fee = transaction.getFee() == null
                       ? BigDecimal.ZERO
                       : transaction.getFee();

        // Both add to balance, as debits are already negatively values.
        accountBalanceAdderByIBAN.addToBalance(transaction.getAccountIban(), transaction.getAmount().subtract(fee));

    }

    private Transaction fillMissingValues(@NonNull Transaction transaction) {
        return addFeeIfMissing(assignDateIfMissing(assignReferenceIfMissing(transaction)));
    }

    private Transaction addFeeIfMissing(@NonNull Transaction transaction) {
        if (transaction.getFee() == null) {
            return Transaction.builder()
                    .reference(transaction.getReference())
                    .accountIban(transaction.getAccountIban())
                    .amount(transaction.getAmount())
                    .date(transaction.getDate())
                    .fee(BigDecimal.valueOf(0))
                    .description(transaction.getDescription())
                    .build();
        }
        return transaction;
    }

    private Transaction assignDateIfMissing(@NonNull Transaction transaction) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        if (StringUtils.isBlank(transaction.getDate())){
            return Transaction.builder()
                              .reference(transaction.getReference())
                              .accountIban(transaction.getAccountIban())
                              .amount(transaction.getAmount())
                              .date(sdf.format(new Date()))
                              .fee(transaction.getFee())
                              .description(transaction.getDescription())
                              .build();
        }
        return transaction;
    }

    private Transaction assignReferenceIfMissing(@NonNull Transaction transaction) {
        if (StringUtils.isBlank(transaction.getReference())) {
            return Transaction.builder()
                              .reference(UUID.randomUUID().toString())
                              .accountIban(transaction.getAccountIban())
                              .amount(transaction.getAmount())
                              .date(transaction.getDate())
                              .fee(transaction.getFee())
                              .description(transaction.getDescription())
                              .build();
        }
        return transaction;
    }

    //We could create a separate validator if this class keeps growing
    private void validateTransaction(Transaction transaction) throws ValidationException {
        validateTransactionFunds(transaction);
        validateTransactionReference(transaction);
        validateAmount(transaction);
        validateDateFormat(transaction);
    }

    private void validateTransactionFunds(@NonNull Transaction transaction) throws ValidationException {
        if (transaction.getFee() != null && accountSupplierByIBAN.get(transaction.getAccountIban()).getBalance()
                .subtract(transaction.getAmount().negate()
                                .subtract(transaction.getFee())).compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("VAL001", "Insufficient funds");
        }
        else if (accountSupplierByIBAN.get(transaction.getAccountIban()).getBalance().add(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("VAL001", "Insufficient funds");
        }
    }

    private void validateTransactionReference(@NonNull Transaction transaction) throws ValidationException {
        if (repository.getAll().stream().anyMatch(t -> t.getReference().equals(transaction.getReference())))
            throw new ValidationException("VAL004", "Reference already in use");
    }

    private void validateAmount(@NonNull Transaction transaction) throws ValidationException {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) == 0)
            throw new ValidationException("VAL005", "Can't send a $0 transaction");
    }

    private void validateDateFormat(@NonNull Transaction transaction) throws ValidationException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.parse(transaction.getDate());
        }  catch (ParseException e) {
            throw new ValidationException("VAL007", "Date format is incorrect, expected is \"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\"");
        }
    }

}
