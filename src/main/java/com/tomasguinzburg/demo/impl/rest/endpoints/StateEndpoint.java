package com.tomasguinzburg.demo.impl.rest.endpoints;

import com.google.gson.Gson;
import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import com.tomasguinzburg.demo.core.state.StateSupplier;
import com.tomasguinzburg.demo.core.state.models.State;
import com.tomasguinzburg.demo.core.state.models.StateQuery;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class StateEndpoint {

    private final StateSupplier stateSupplier;
    private final Gson gson;

    @Inject
    public StateEndpoint(StateSupplier stateSupplier, Gson gson) {
        this.stateSupplier = stateSupplier;
        this.gson = gson;
    }

    public State getState(Request req, Response res) throws ValidationException {
        return stateSupplier.get(gson.fromJson(req.body(), StateQuery.class));
    }
}
