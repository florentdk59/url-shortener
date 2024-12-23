package com.project.urlshortener.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Access to some of the application parameters (application.yml).
 * @param baseUrl (String) Base of the short url. This is the base for the short url. Only the url token is missing.
 * @param token (Token) Tokem related parameters.
 */
@ConfigurationProperties("url-shortener")
public record UrlShortenerProperties (String baseUrl, Token token) {

	/**
	 *
	 * @param length (int) Length of an url token. This is the size of short url token.
	 * @param maxAttempts (int) Maximum number of attempts to create an url token.
	 * @param characters (String) Possible characters for an url token. These are all the available characters that can be used to create a short url token.
	 */
	public record Token(Integer length, Integer maxAttempts, String characters) {}

}
