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
    //This shouldn't be here, but I don't have a lot of time and I rather not spend it fighting with GSON
    public String toJSON(){
        return "{\n    \"code\": \"" + code + "\",\n    \"message\": \"" + message + "\"\n}";
    }
}
