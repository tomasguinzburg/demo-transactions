package com.tomasguinzburg.demo.core.transactions.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionQuery {
    private String accountIban;
    private String sorting;
}
