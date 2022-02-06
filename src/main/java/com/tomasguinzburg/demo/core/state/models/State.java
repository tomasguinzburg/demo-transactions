package com.tomasguinzburg.demo.core.state.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class State {

        private final String reference;
        private final String status;
        private final BigDecimal amount;
        private final BigDecimal fee;

}
