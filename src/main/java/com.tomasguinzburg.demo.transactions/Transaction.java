package com.tomasguinzburg.demo.transactions;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

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
