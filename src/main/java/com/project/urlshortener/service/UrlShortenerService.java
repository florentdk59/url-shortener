package com.project.urlshortener.service;

import com.project.urlshortener.exception.*;

/**
 * Service to control the creation of short urls and the retrieval of complete urls.
 */
public interface UrlShortenerService {

    /**
     * Obtain a short url for a complete url.
     *
     * @param originalUrl the complete url for which we want a short url.
     * @return the short url which is immediately usable.
     * @throws ShortUrlInvalidUrlException if the submitted url is not a valid url.
     */
    String obtainShortUrlForOriginalCompleteUrl(final String originalUrl) throws ShortUrlInvalidUrlException;

    /**
     * Get the original complete url for a short url token.
     *
     * @param shortUrlToken the short url token.
     * @return the original complete url.
     * @throws ShortUrlTokenNotFoundException the token does not exist and no complete url could be found.
     * @throws ShortUrlInvalidTokenException the token is empty or null.
     */
    String getOriginalUrlForShortUrlToken(final String shortUrlToken) throws ShortUrlTokenNotFoundException, ShortUrlInvalidTokenException;
}
