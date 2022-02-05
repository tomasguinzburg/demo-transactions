package com.tomasguinzburg.demo.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public class ValidationException extends Exception {
    public final String code;
    public final String message;

    public String toJSON(){
        return "{\n    \"code\": \"" + code + "\",\n    \"message\": \"" + message + "\"\n}";
    }
}
