package com.tomasguinzburg.demo.core.transactions;

import lombok.Data;

@Data
public class TransactionQuery {
    private String accountIban;
    private String sorting;
}
