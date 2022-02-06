package com.tomasguinzburg.demo.impl.repository.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
