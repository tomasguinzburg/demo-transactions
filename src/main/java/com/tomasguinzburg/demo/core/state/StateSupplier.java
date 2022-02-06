package com.tomasguinzburg.demo.core.state;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.state.models.State;
import com.tomasguinzburg.demo.core.state.models.StateQuery;

public interface StateSupplier {
    State get(StateQuery query) throws ValidationException;
}
