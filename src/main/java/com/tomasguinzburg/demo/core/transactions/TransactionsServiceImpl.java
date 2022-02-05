package com.tomasguinzburg.demo.core.transactions;

import com.tomasguinzburg.demo.core.accounts.AccountsService;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repository.TransactionsRepository;
import spark.utils.StringUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository repository;
    private final AccountsService accountsService;

    @Inject
    public TransactionsServiceImpl(TransactionsRepository repository, AccountsService accountsService){
        this.repository = repository;
        this.accountsService = accountsService;
    }

    @Override
    public Long create(final Transaction transaction_prototype) throws ValidationException {
        Transaction transaction = assignReferenceIfMissing(transaction_prototype);
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
    public List<Transaction> getByQuery(TransactionQuery query) {
        List<Transaction> transactions = this.getAll();
        if (StringUtils.isNotBlank(query.getAccountIban())){
            transactions = transactions.stream()
                                       .filter(t -> t.getAccountIban().equals(query.getAccountIban()))
                                       .collect(Collectors.toList());
        }
        if (query.getSorting().equals("ascending")) {
            transactions = transactions.stream()
                                       .sorted(Comparator.comparing(Transaction::getAmount))
                                       .collect(Collectors.toList());
        } else if (query.getSorting().equals("descending")) {
            transactions = transactions.stream()
                                       .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                                       .collect(Collectors.toList());
        }
        return transactions;
    }


    private void processTransaction(Transaction transaction) throws ValidationException {
        BigDecimal fee = transaction.getFee() == null
                       ? BigDecimal.ZERO
                       : transaction.getFee();

        // To be very rigorous, we should check that the substracted fee does not change
        // the operation sign. But in the spec we are told that, by definition,
        // a transaction is a debit or a credit according to the sign of the amount field.
        // I will go for exactly what the test says; but it could lead to problems when, for example
        // amount = 3 fee = 3.18 gives us a credit that's actually deducting 18 cents from the account.
        Boolean debit = transaction.getAmount().compareTo(BigDecimal.ZERO) < 0;
        Boolean credit = transaction.getAmount().compareTo(BigDecimal.ZERO) > 0;


        // Both add to balance, as debits are already negatively values.
        if (debit) {
            accountsService.addToBalance(transaction.getAccountIban(), transaction.getAmount().subtract(transaction.getFee()));
        } else if (credit) {
            accountsService.addToBalance(transaction.getAccountIban(), transaction.getAmount().subtract(transaction.getFee()));
        }

    }


    /**
     * If the reference number is missing, creates a new transaction with an auto generated one.
     * @param transaction The original transaction
     * @return Either the original transaction, or a copy with an autogenerated reference number.
     */
    private Transaction assignReferenceIfMissing(Transaction transaction) {
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

    /**
     * Applies all validation required
     * @param transaction the transaction to validate
     * @throws ValidationException the appropiate validation exception
     */
    private void validateTransaction(Transaction transaction) throws ValidationException {
        validateTransactionFunds(transaction);
        validateTransactionReference(transaction);
        validateAmount(transaction);
    }

    /**
     * Validates account has sufficient founds to make the transaction and pay the fee
     * @param transaction the transaction to validate
     * @throws ValidationException code VAL001
     * @implNote BigDecimal arithmetic is a pain, read carefully
     */
    private void validateTransactionFunds(Transaction transaction) throws ValidationException {
        if (accountsService.get(transaction.getAccountIban()).getBalance()
                .subtract(transaction.getAmount().negate()
                                .subtract(transaction.getFee())).compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("VAL001", "Insufficient funds");
        }
    }

    private void validateAmount(Transaction transaction) throws ValidationException {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) == 0)
            throw new ValidationException("VAL005", "Can't send a $0 transaction");
    }

    /**
     * Validates the transaction reference is not already in use on the system, as it should be UNIQUE by spec
     * @param transaction the transaction to validate
     * @throws ValidationException code VAL004
     */
    private void validateTransactionReference(Transaction transaction) throws ValidationException {
        if (repository.getAll().stream().anyMatch(t -> t.getReference().equals(transaction.getReference())))
            throw new ValidationException("VAL004", "Reference already in use");
    }

}
