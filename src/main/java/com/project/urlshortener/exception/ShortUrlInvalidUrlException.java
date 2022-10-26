package com.project.urlshortener.exception;

import com.project.urlshortener.common.exception.ExceptionWithMessageKey;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An Exception for when an url is invalid.
 */
@Data
@Setter(AccessLevel.NONE)
public class ShortUrlInvalidUrlException extends Exception implements ExceptionWithMessageKey {

    private static final String MESSAGE_KEY_INVALID_URL = "error.shorturl.InvalidUrl";

    /**
     * The value of the url that was invalid.
     */
    private final String url;

    public ShortUrlInvalidUrlException(final String url) {
        super();
        this.url = url;
    }

    @Override
    public String getMessageKey() {
        return MESSAGE_KEY_INVALID_URL;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("url", url)
                .toString();
    }

    @Override
    public String[] getMessageArguments() {
        return new String[] { this.url };
    }
}
