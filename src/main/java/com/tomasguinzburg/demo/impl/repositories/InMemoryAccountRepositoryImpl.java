package com.tomasguinzburg.demo.impl.repositories;

import com.google.common.collect.MoreCollectors;
import com.tomasguinzburg.demo.core.accounts.models.Account;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repositories.AccountRepository;
import com.tomasguinzburg.demo.impl.repositories.entities.AccountEntity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
public class InMemoryAccountRepositoryImpl implements AccountRepository {
    private final AtomicLong idSequence;
    private final Map<Long, AccountEntity> memoryStorage;

    @Inject
    public InMemoryAccountRepositoryImpl(Integer capacity) {
        idSequence = new AtomicLong(0);
        memoryStorage = new ConcurrentHashMap<>(capacity);
        this.populate();
    }

    //FIXME in a real application this method should not exist and accounts created via due process.
    //      I didn't bother opening and endpoint for this, but it should be pretty quick to do if required.
    //      Also in a real application this repository implementation would probably be an HTTPClient connecting
    //      to a different microservice, named demo-accounts.
    private void populate(){
        Account account1 = Account.builder()
                                  .ownerUserID(1L)
                                  .iban("ES9820385778983000760236")
                                  .balance(new BigDecimal(10000000))
                                  .label("Loaded account")
                                  .build();
        Account account2 = Account.builder()
                                  .ownerUserID(2L)
                                  .iban("ES9820385778983000760237")
                                  .balance(new BigDecimal(100))
                                  .label("Poor account")
                                  .build();
        this.save(account1);
        this.save(account2);
    }

    /**
     * Gets the account by ID
     *
     * @param ID the account ID
     * @return the corresponding Account
     */
    @Override
    public Account get(long ID) {
        AccountEntity entity = memoryStorage.get(ID);
        return Account.builder()
                      .ownerUserID(entity.getOwnerUserID())
                      .iban(entity.getIban())
                      .label(entity.getLabel())
                      .balance(entity.getBalance())
                      .build();
    }

    /**
     * Gets the account by iban
     *
     * @param iban the account iban
     * @return the corresponding Account
     * @implNote Will throw illegal argument exception if more than one account shares IBAN.
     *           This should never happen anyway.
     */
    @Override
    public Account get(String iban) throws ValidationException {
        try {
                return this.getAll().stream()
                        .filter(a -> a.getIban().equals(iban))
                        .collect(MoreCollectors.onlyElement());
        } catch (NoSuchElementException e){
            throw new ValidationException("VAL002", "There are no accounts with this IBAN");
        } catch(IllegalArgumentException ie){
            throw new ValidationException("VAL003", "There are multiple accounts with the same IBAN. Call your bank");
        }
    }

    /**
     * Gets all accounts
     *
     * @return all accounts in the system
     */
    @Override
    public List<Account> getAll() {
        return memoryStorage.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .map(e -> Account.builder()
                                 .ownerUserID(e.getOwnerUserID())
                                 .iban(e.getIban())
                                 .label(e.getLabel())
                                 .balance(e.getBalance())
                                 .build())
                .collect(Collectors.toList());
    }

    /**
     * Updates only the balance of an account.
     *
     * @param ID      the account's ID
     * @param balance the account's new balance
     * @return the updated account
     */
    @Override
    public Account updateBalance(Long ID, BigDecimal balance) {
        AccountEntity original_entity = memoryStorage.get(ID);
        AccountEntity updated_entity = AccountEntity.builder()
                                                    .ID(original_entity.getID())
                                                    .ownerUserID(original_entity.getOwnerUserID())
                                                    .iban(original_entity.getIban())
                                                    .label(original_entity.getLabel())
                                                    .balance(balance)
                                                    .build();
        memoryStorage.put(ID, updated_entity);
        return this.get(ID);
    }

    /**
     * Updates only the balance of an account.
     *
     * @param iban    the account's iban
     * @param balance the account's new balance
     * @return the updated account
     */
    @Override
    public Account updateBalance(String iban, BigDecimal balance) throws ValidationException {
        try{
            AccountEntity original_entity = memoryStorage.entrySet()
                                                         .stream()
                                                         .map(Map.Entry::getValue)
                                                         .filter(v -> v.getIban().equals(iban))
                                                         .collect(MoreCollectors.onlyElement());
            AccountEntity updated_entity = AccountEntity.builder()
                                                        .ID(original_entity.getID())
                                                        .ownerUserID(original_entity.getOwnerUserID())
                                                        .iban(original_entity.getIban())
                                                        .label(original_entity.getLabel())
                                                        .balance(balance)
                                                        .build();
            memoryStorage.put(original_entity.getID(), updated_entity);
            return this.get(original_entity.getID());
        } catch (NoSuchElementException e){
            throw new ValidationException("VAL002", "There are no accounts with this IBAN");
        } catch(IllegalArgumentException ie){
            throw new ValidationException("VAL003", "There are multiple accounts with the same IBAN. Call your bank");
        }
    }

    /**
     * Saves a new account, if IBAN is unique
     *
     * @param account the account to create
     * @return the account's ID
     */
    @Override
    public Long save(Account account) {
        Long ID = idSequence.incrementAndGet();
        AccountEntity entity = AccountEntity.builder()
                                            .ID(ID)
                                            .ownerUserID(account.getOwnerUserID())
                                            .iban(account.getIban())
                                            .label(account.getLabel())
                                            .balance(account.getBalance())
                                            .build();

        memoryStorage.put(ID, entity);
        return ID;
    }
}
