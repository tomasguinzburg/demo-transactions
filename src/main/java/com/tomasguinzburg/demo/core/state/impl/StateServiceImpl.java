package com.tomasguinzburg.demo.core.state.impl;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.repositories.TransactionRepository;
import com.tomasguinzburg.demo.core.state.models.State;
import com.tomasguinzburg.demo.core.state.models.StateQuery;
import com.tomasguinzburg.demo.core.transactions.models.Transaction;
import com.tomasguinzburg.demo.core.state.StateSupplier;
import spark.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.*;

@Singleton
public class StateServiceImpl implements StateSupplier {

    TransactionRepository repository;

    @Inject
    public StateServiceImpl(TransactionRepository repository){
        this.repository = repository;
    }

    @Override
    public State get(StateQuery query) throws ValidationException {
        try {
            Transaction transaction = repository.get(query.getReference());

            State.StateBuilder builder = State.builder();
            checkValidity(builder, query);
            buildAmountAndFee(builder, query, transaction);
            buildStatus(builder, query, transaction);

            return builder.reference(query.getReference()).build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return State.builder().reference(query.getReference()).status("INVALID").build();
        }
    }

    private void checkValidity(State.StateBuilder builder, StateQuery query) throws ValidationException {
        List<String> authorized = new ArrayList<>(Arrays.asList("CLIENT", "ATM", "INTERNAL"));
        if (StringUtils.isBlank(query.getChannel()) || !authorized.contains(query.getChannel()))
            throw new ValidationException("VAL006", "Not authorized");

    }

    private void buildAmountAndFee(State.StateBuilder builder, StateQuery query, Transaction transaction) {
        if (query.getChannel().equals("ATM") || query.getChannel().equals("CLIENT"))
            builder.amount(transaction.getAmount().subtract(transaction.getFee()));
        else
            builder.amount(transaction.getAmount()).fee(transaction.getFee());
    }

    private void buildStatus(State.StateBuilder builder, StateQuery query, Transaction transaction) {
        if (isPastDate(transaction.getDate()))
            builder.status("SETTLED");
        else if (query.getChannel().equals("ATM") || isToday(transaction.getDate()))
            builder.status("PENDING");
        else
            builder.status("FUTURE");
    }

    private boolean isPastDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String today = sdf.format(new Date()).substring(0, 10).replace("-", "");
        String date_day = date.substring(0,10).replace("-","");
        return Integer.parseInt(today) > Integer.parseInt(date_day);
    }

    private boolean isToday(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String today = sdf.format(new Date()).substring(0, 10).replace("-", "");
        String date_day = date.substring(0,10).replace("-","");
        return Integer.parseInt(today) == Integer.parseInt(date_day);
    }
}
