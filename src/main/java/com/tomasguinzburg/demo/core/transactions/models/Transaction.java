package com.tomasguinzburg.demo.core.transactions.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class Transaction {
    private final String reference;
    private final @NonNull String accountIban;
    private final String date;
    private final @NonNull BigDecimal amount;
    private final BigDecimal fee;
    private final String description;

}
