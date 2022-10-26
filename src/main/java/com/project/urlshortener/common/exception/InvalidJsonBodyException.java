package com.project.urlshortener.common.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * An Exception for a JSON body that is invalid (ex.: empty body, bad json syntax, ...).<br/>
 * This exception is a RuntimeException.
 */
@Data
@Setter(AccessLevel.NONE)
public class InvalidJsonBodyException extends RuntimeException implements ExceptionWithMessageKey {

    private static final String INVALID_JSON_BODY = "error.json.body.invalid";

    /**
     * Default constructor.
     *
     * @param e Exception encapsulated error.
     */
    public InvalidJsonBodyException(Exception e) {
        super(e);
    }

    @Override
    public String getMessageKey() {
        return INVALID_JSON_BODY;
    }

}
