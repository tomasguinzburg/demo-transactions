package com.tomasguinzburg.demo.impl.rest;

import spark.ExceptionHandlerImpl;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

/**
 * @implNote This class should not exist in any serious project, but at this point,
 *           writing custom validation for all non-nullable fields would take a lot more time than I'm able to spend
 *           within the given deadlines.
 *           I'm pretty confident no unexpected null pointer will be referenced, but fingers crossed
 */
public class NullPointerExceptionHandler extends ExceptionHandlerImpl<NullPointerException> {

    /**
     * Initializes the filter with the provided exception type
     *
     * @param exceptionClass Type of exception
     */
    @Inject
    public NullPointerExceptionHandler(Class<NullPointerException> exceptionClass) {
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
    public void handle(NullPointerException exception, Request request, Response response) {
        response.body("{\"code\": \"NPE000\",\"message\": \"Something went wrong while processing your request. Are you" +
                "sure you are sending all required fields? That would be the most probable reason\"");
        response.type("application/json");
        response.status(400);
    }
}
