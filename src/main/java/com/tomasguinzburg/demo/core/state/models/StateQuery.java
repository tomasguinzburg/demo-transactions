package com.tomasguinzburg.demo.core.state.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class StateQuery {
    private final @NonNull String reference;
    private final String channel;
}
