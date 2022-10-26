package com.project.urlshortener.model.properties;

import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Access to some of the application parameters (application.yml).
 */
@Component
@ConfigurationProperties(ignoreUnknownFields = true)
@Data
@Setter(AccessLevel.NONE)
public class UrlShortenerProperties {

	/**
	 * Base of the short url.<br/>
	 * This is the base for the short url. Only the url token is missing.
	 */
	@Value("${urlshortener.baseurl}")
	private String urlShortenerBaseUrl;

	/**
	 * Length of an url token.<br/>
	 * this is the size of short url token.
	 */
	@Value("${urlshortener.token.length}")
	private int urlshortenerTokenLength;

	/**
	 * Maximum number of attempts to create an url token.
	 */
	@Value("${urlshortener.token.maxattempts}")
	private int urlshortenerTokenMaxAttempts;

    /**
     * Possible characters for an url token.<br/>
     * These are all the available characters that can be used to create a short url token.
     */
    @Value("${urlshortener.token.characters}")
    private String urlshortenerTokenCharacters;


}
