package com.project.urlshortener.repository.impl;

import com.project.urlshortener.common.utils.ArgumentUtils;
import com.project.urlshortener.exception.ShortUrlTokenAlreadyUsedException;
import com.project.urlshortener.exception.ShortUrlTokenCannotBeCreatedException;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.model.properties.UrlShortenerProperties;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.service.StringTokenService;
import com.project.urlshortener.repository.ShortUrlDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * Manipulates ShortUrlRepository to access the database.<br/>
 * Implementation of ShortUrlDao.
 */
@Service
public class ShortUrlDaoImpl implements ShortUrlDao {

    private static final Logger LOGGER = LogManager.getLogger(ShortUrlDaoImpl.class);

    /**
     * Access to some of the application parameters.
     */
    private final UrlShortenerProperties urlShortenerProperties;

    /**
     * Service to create tokens of characters.
     */
    private final StringTokenService stringTokenService;

    /**
     * Spring Data Repository for the SHORT_URL table.
     */
    private final ShortUrlRepository urlTokensRepository;

    /**
     * Default constructor for ShortUrlDaoImpl.
     *
     * @param urlTokensRepository instance of ShortUrlRepository.
     * @param stringTokenService instance of StringTokenService.
     * @param urlShortenerProperties instance of UrlShortenerProperties.
     */
    @Autowired
    public ShortUrlDaoImpl(final ShortUrlRepository urlTokensRepository, final StringTokenService stringTokenService, final UrlShortenerProperties urlShortenerProperties) {
        this.urlTokensRepository = urlTokensRepository;
        this.stringTokenService = stringTokenService;
        this.urlShortenerProperties = urlShortenerProperties;
    }

    /**
     * Searches for a ShortUrlEntity in the database matching a specific token value.
     *
     * @param token the value of the token to look for.
     * @return the ShortUrlEntity found in the database. Can return null if the token was not found.
     */
    @Override
    public ShortUrlEntity findExistingShortUrlEntityByToken(final String token) {
        ArgumentUtils.requireNonBlank(token, "token");

        return urlTokensRepository.findByToken(token);
    }

    /**
     * Searches for a ShortUrlEntity in the database matching a specific original url value.
     *
     * @param originalUrl the value of the original url to look for.
     * @return the ShortUrlEntity found in the database. Can return null if the url was not found.
     */
    @Override
    public ShortUrlEntity findExistingShortUrlEntityByOriginalUrl(final String originalUrl) {
        ArgumentUtils.requireNonBlank(originalUrl, "originalUrl");

        return urlTokensRepository.findByOriginalUrl(originalUrl);
    }

    /**
     * Creates a brand new token for an original url value and saves a ShortUrlEntity in the database.<br/>
     * Double checks if the newly created token is already used in the database. If it is already used, the method will fail with ShortUrlTokenAlreadyUsedException.<br/>
     * If the newly created token is null or empty, the method will fail with ShortUrlTokenCannotBeCreatedException.<br/>
     * This method will retry a couple of times (see maxAttemptsExpression) if it fails with any exception.<br/>
     * If this method fails too many times (more than maxAttemptsExpression), the last exception thrown will escape the method to reach the caller.
     *
     * @param originalUrl the value of the original url.
     * @return the ShortUrlEntity created in the database.
     */
    @Retryable(maxAttemptsExpression = "#{@urlShortenerProperties.getUrlshortenerTokenMaxAttempts()}")
    public ShortUrlEntity createNewShortUrlEntityRetryable(final String originalUrl) {
        String shortUrlToken = stringTokenService.createStringToken(urlShortenerProperties.getUrlshortenerTokenCharacters(), urlShortenerProperties.getUrlshortenerTokenLength());
        if (StringUtils.isBlank(shortUrlToken)) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("createNewShortUrlEntityRetryable : for originalUrl[%s] the token was null empty or blank [%s]", originalUrl, shortUrlToken));
            }
            throw new ShortUrlTokenCannotBeCreatedException(originalUrl);
        }

        if (urlTokensRepository.findByToken(shortUrlToken) != null) {
            // token already taken
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("createNewShortUrlEntityRetryable : for originalUrl[%s] the token [%s] was already taken", originalUrl, shortUrlToken));
            }
            throw new ShortUrlTokenAlreadyUsedException(shortUrlToken, originalUrl);
        }

        // save new short url to the database
        return urlTokensRepository.save(ShortUrlEntity.builder().token(shortUrlToken).originalUrl(originalUrl).build());
    }

}
