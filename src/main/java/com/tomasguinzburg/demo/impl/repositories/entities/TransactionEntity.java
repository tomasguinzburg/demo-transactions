package com.tomasguinzburg.demo.impl.repositories.entities;


import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
public class TransactionEntity {
    private final @NonNull Long ID;
    private final String reference;
    private final @NonNull String accountIban;
    private final String date;
    private final @NonNull BigDecimal amount;
    private final BigDecimal fee;
    private final String description;

    private TransactionEntity( Long ID
                             , String reference
                             , String accountIban
                             , String date
                             , BigDecimal amount
                             , BigDecimal fee
                             , String description
                             ) {
        this.ID = ID;
        this.reference = reference;
        this.accountIban = accountIban;
        this.date = date;
        this.amount = amount;
        this.fee = fee;
        this.description = description;
    }

    // Manual implementation to show that I can create a builder pattern without abusing lombok :)
    // This, with added getters, setters, constructors, toStrings, hashCodes, null checks... it adds up.
    // Most importantly, changing just one field in a class can make for a huge pull request with lots of lines to review
    // I'm use lombok because it simplifies all of that.
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long ID;
        private String reference;
        private String accountIban;
        private String date;
        private BigDecimal amount;
        private BigDecimal fee;
        private String description;

        public TransactionEntity build() {
            return new TransactionEntity( this.ID
                                        , this.reference
                                        , this.accountIban
                                        , this.date
                                        , this.amount
                                        , this.fee
                                        , this.description
                                        );
        }

        public Builder ID(Long ID) {
            this.ID = ID;
            return this;
        }

        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder accountIban(String accountIban) {
            this.accountIban = accountIban;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder fee(BigDecimal fee) {
            this.fee = fee;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }
    }

}
