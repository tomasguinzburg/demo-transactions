package com.tomasguinzburg.demo.impl.repositories.entities;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AccountEntity {
    private final Long ID;
    private final Long ownerUserID;
    private final String iban;
    private final BigDecimal balance;
    private final String label;

}
