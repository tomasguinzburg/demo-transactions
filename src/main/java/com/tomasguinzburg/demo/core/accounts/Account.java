package com.tomasguinzburg.demo.core.accounts;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class Account {
    private final Long ownerUserID;
    private final String iban;
    private final BigDecimal balance;
    private final String label;

}
