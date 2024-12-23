package com.project.urlshortener.exception;

import com.project.urlshortener.common.exception.ExceptionWithMessageKey;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An Exception for when a token is invalid.
 */
@Getter
public class ShortUrlInvalidTokenException extends Exception implements ExceptionWithMessageKey {

    private static final String MESSAGE_KEY_INVALID_TOKEN = "error.shorturl.InvalidToken";

    /**
     * The value of the token that was invalid.
     */
    private final String token;

    public ShortUrlInvalidTokenException(final String token) {
        super();
        this.token = token;
    }

    @Override
    public String getMessageKey() {
        return MESSAGE_KEY_INVALID_TOKEN;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("token", token)
                .toString();
    }

    @Override
    public String[] getMessageArguments() {
        return new String[] { this.token };
    }
}
