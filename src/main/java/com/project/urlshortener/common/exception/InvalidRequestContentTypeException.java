package com.project.urlshortener.common.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * An Exception for a content type is rejected in a request.<br/>
 * This exception is a RuntimeException.
 */
@Data
@Setter(AccessLevel.NONE)
public class InvalidRequestContentTypeException extends RuntimeException implements ExceptionWithMessageKey {

    private static final String INVALID_CONTENT_TYPE = "error.rest.content.type";

    /**
     * Default constructor.
     *
     * @param e Exception encapsulated error.
     */
    public InvalidRequestContentTypeException(Exception e) {
        super(e);
    }

    @Override
    public String getMessageKey() {
        return INVALID_CONTENT_TYPE;
    }

}
