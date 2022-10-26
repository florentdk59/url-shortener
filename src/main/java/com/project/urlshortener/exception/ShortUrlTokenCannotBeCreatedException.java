package com.project.urlshortener.exception;

import com.project.urlshortener.common.exception.ExceptionWithMessageKey;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An Exception for when a short url token cannot be created for technical issues.<br/>
 * This error is a RuntimeException.
 */
public class ShortUrlTokenCannotBeCreatedException extends RuntimeException implements ExceptionWithMessageKey {

    private static final String MESSAGE_KEY_TOKEN_CANNOT_BE_CREATED = "error.shorturl.TokenCannotBeCreated";

    /**
     * The value of the url for which the token was created.
     */
    private final String originalUrl;

    /**
     * Default constructor for ShortUrlTokenCannotBeCreatedException.
     * @param originalUrl the value of the token created for the url.
     */
    public ShortUrlTokenCannotBeCreatedException(final String originalUrl) {
        super();
        this.originalUrl = originalUrl;
    }

    @Override
    public String getMessageKey() {
        return MESSAGE_KEY_TOKEN_CANNOT_BE_CREATED;
    }

    @Override
    public String[] getMessageArguments() {
        return new String[] { this.originalUrl };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("originalUrl", originalUrl)
                .toString();
    }

}
