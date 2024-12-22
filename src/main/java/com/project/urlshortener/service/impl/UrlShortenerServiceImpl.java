package com.project.urlshortener.service.impl;

import com.project.urlshortener.common.utils.ArgumentUtils;
import com.project.urlshortener.exception.*;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.model.properties.UrlShortenerProperties;
import com.project.urlshortener.repository.ShortUrlDao;
import com.project.urlshortener.service.UrlShortenerService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to control the creation of short urls and the retrieval of complete urls.<br/>
 * Implements UrlShortenerService.
 */
@Service
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService  {

    /** Access to some of the application parameters. */
    private final UrlShortenerProperties urlShortenerProperties;

    /** Main functions to access the database. */
    private final ShortUrlDao shortUrlDao;

    /** Apache commons validation routines for URLs. */
    private final UrlValidator urlValidator;


    @Override
    public String obtainShortUrlForOriginalCompleteUrl(final String originalUrl) throws ShortUrlInvalidUrlException {
        ArgumentUtils.requireNonBlank(originalUrl, "originalUrl");

        // validate the url
        if (!isUrlValid(originalUrl)) {
            throw new ShortUrlInvalidUrlException(originalUrl);
        }

        try {
            // obtain the token
            String shortUrlToken = findOrCreateShortUrlToken(originalUrl);
            ArgumentUtils.requireNonBlank(shortUrlToken, "shortUrlToken");

            // build the url
            return buildShortUrlForToken(shortUrlToken);

        } catch(ShortUrlTokenAlreadyUsedException e) {
            throw new ShortUrlTokenCannotBeCreatedException(originalUrl);
        }

    }

    @Override
    public String getOriginalUrlForShortUrlToken(final String shortUrlToken) throws ShortUrlTokenNotFoundException, ShortUrlInvalidTokenException {
        // validate the url
        if (!isTokenValid(shortUrlToken)) {
            throw new ShortUrlInvalidTokenException(shortUrlToken);
        }

        return Optional.ofNullable(shortUrlDao.findExistingShortUrlEntityByToken(shortUrlToken)).orElseThrow(() -> new ShortUrlTokenNotFoundException(shortUrlToken)).getOriginalUrl();

    }

    /**
     * Obtains a short url token for an original url.<br/>
     * It will try to find an already existing short url token for an original url.<br/>
     * If it cannot find one, it will trigger the creation of a brand-new token.<br/>
     * Can throw ShortUrlTokenCannotBeCreatedException if the token cannot be created for technical issues.
     *
     * @param originalUrl the complete url for which we want a short url token.
     * @return a short url token
     */
    protected String findOrCreateShortUrlToken(final String originalUrl) {
        ArgumentUtils.requireNonBlank(originalUrl, "originalUrl");
        ArgumentUtils.requireNonBlank(urlShortenerProperties.getUrlshortenerTokenCharacters(), "urlShortenerProperties.getUrlshortenerTokenCharacters()");
        ArgumentUtils.requireStrictlyPositiveValue(urlShortenerProperties.getUrlshortenerTokenLength(), "urlShortenerProperties.getUrlshortenerTokenLength()");

        // search in the database for the token if it already exists for this url?
        // OR create a new token if there wasn't already one
        ShortUrlEntity shortUrlEntity = Optional.ofNullable(shortUrlDao.findExistingShortUrlEntityByOriginalUrl(originalUrl)).orElseGet(() -> shortUrlDao.createNewShortUrlEntityRetryable(originalUrl));
        if (shortUrlEntity == null) {
            throw new ShortUrlTokenCannotBeCreatedException(originalUrl);
        }
        return shortUrlEntity.getToken();
    }

    /**
     * Creates a short url using a short url token<br/>
     * Combines the token with the base url (from UrlShortenerProperties) to create the final usable short url.
     *
     * @param shortUrlToken the token for the short url.
     * @return the final short url, with the base url and the token together.
     */
    protected String buildShortUrlForToken(final String shortUrlToken) {
        ArgumentUtils.requireNonBlank(shortUrlToken, "shortUrlToken");
        ArgumentUtils.requireNonBlank(urlShortenerProperties.getUrlShortenerBaseUrl(), "urlShortenerProperties.getUrlShortenerBaseUrl()");

        String baseUrl = urlShortenerProperties.getUrlShortenerBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + shortUrlToken;
    }

    /**
     * Verifies wheteher an url is valid or not, according to apache commons UrlValidator.
     *
     * @param url the tested url.
     * @return true if the url is valid, false it is not.
     */
    protected boolean isUrlValid(final String url) {
        return this.urlValidator.isValid(url);
    }

    /**
     * Verifies whether a token is valid or not.
     *
     * @param token the tested token.
     * @return true if the url is not null, not empty, not blank, false it is.
     */
    protected boolean isTokenValid(final String token) {
        return StringUtils.isNotBlank(token);
    }

}
