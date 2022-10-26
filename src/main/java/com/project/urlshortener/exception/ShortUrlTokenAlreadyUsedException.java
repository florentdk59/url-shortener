package com.project.urlshortener.exception;

import com.project.urlshortener.common.exception.ExceptionWithMessageKey;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An Exception for when a short url token was created, but it is already used for another url.<br/>
 * This error is a RuntimeException.
 */
public class ShortUrlTokenAlreadyUsedException extends RuntimeException implements ExceptionWithMessageKey {

    private static final String MESSAGE_KEY_TOKEN_ALREADY_USED = "error.shorturl.TokenAlreadyUsed";

    /**
     * The value of the url for which the token was created.
     */
    private final String originalUrl;

    /**
     * The value of the token created for the url.
     */
    private final String shortUrlToken;

    /**
     * Default constructor for ShortUrlTokenAlreadyUsedException.
     * @param shortUrlToken the value of the url for which the token was created.
     * @param originalUrl the value of the token created for the url.
     */
    public ShortUrlTokenAlreadyUsedException(final String shortUrlToken, final String originalUrl) {
        super();
        this.shortUrlToken = shortUrlToken;
        this.originalUrl = originalUrl;
    }

    @Override
    public String getMessageKey() {
        return MESSAGE_KEY_TOKEN_ALREADY_USED;
    }

    @Override
    public String[] getMessageArguments() {
        return new String[] { this.shortUrlToken, this.originalUrl };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("originalUrl", originalUrl)
                .append("shortUrlToken", shortUrlToken)
                .toString();
    }

}
