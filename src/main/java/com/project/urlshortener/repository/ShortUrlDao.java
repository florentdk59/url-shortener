package com.project.urlshortener.repository;

import com.project.urlshortener.model.entities.ShortUrlEntity;

/**
 * Main functions to access the database.
 */
public interface ShortUrlDao {

    /**
     * Searches for a ShortUrlEntity in the database matching a specific token value.
     *
     * @param token the value of the token to look for.
     * @return the ShortUrlEntity found in the database. Can return null if the token was not found.
     */
    ShortUrlEntity findExistingShortUrlEntityByToken(final String token);

    /**
     * Searches for a ShortUrlEntity in the database matching a specific original url value.
     *
     * @param originalUrl the value of the original url to look for.
     * @return the ShortUrlEntity found in the database. Can return null if the url was not found.
     */
    ShortUrlEntity findExistingShortUrlEntityByOriginalUrl(final String originalUrl);

    /**
     * Creates a brand new token for an original url value and saves a ShortUrlEntity in the database.
     *
     * @param originalUrl the value of the original url.
     * @return the ShortUrlEntity created in the database.
     */
    ShortUrlEntity createNewShortUrlEntityRetryable(final String originalUrl);
}
