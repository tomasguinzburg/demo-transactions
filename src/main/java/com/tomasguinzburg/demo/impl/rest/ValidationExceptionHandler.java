package com.tomasguinzburg.demo.impl.rest;

import com.tomasguinzburg.demo.core.exceptions.ValidationException;
import spark.ExceptionHandlerImpl;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class ValidationExceptionHandler extends ExceptionHandlerImpl<ValidationException> {

    /**
     * Initializes the filter with the provided exception type
     *
     * @param exceptionClass Type of exception
     */
    @Inject
    public ValidationExceptionHandler(Class<ValidationException> exceptionClass) {
        super(exceptionClass);
    }

    /**
     * Invoked when an exception that is mapped to this handler occurs during routing
     *
     * @param exception The exception that was thrown during routing
     * @param request   The request object providing information about the HTTP request
     * @param response  The response object providing functionality for modifying the response
     */
    @Override
    public void handle(ValidationException exception, Request request, Response response) {
        response.body(exception.toJSON());
        response.type("application/json");
        response.status(400);
    }
}
