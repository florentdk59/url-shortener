package com.project.urlshortener.exception;

import com.project.urlshortener.common.exception.ExceptionWithMessageKey;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An Exception for when a short url token cannot be found in the database, when trying to get the matching original url.
 */
public class ShortUrlTokenNotFoundException extends Exception implements ExceptionWithMessageKey {

    private static final String MESSAGE_KEY_TOKEN_NOT_FOUND = "error.shorturl.TokenNotFound";

    /**
     * The value of the token which was not found in the database.
     */
    private final String token;

    /**
     * Default constructor for ShortUrlTokenNotFoundException.
     * @param token tvalue of the token which was not found in the database.
     */
    public ShortUrlTokenNotFoundException(final String token) {
        super();
        this.token = token;
    }

    @Override
    public String getMessageKey() {
        return MESSAGE_KEY_TOKEN_NOT_FOUND;
    }

    @Override
    public String[] getMessageArguments() {
        return new String[] { this.token };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("token", token)
                .toString();
    }

}
