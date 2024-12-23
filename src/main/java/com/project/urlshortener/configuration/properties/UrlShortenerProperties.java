package com.project.urlshortener.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Access to some of the application parameters (application.yml).
 * @param baseUrl (String) Base of the short url. This is the base for the short url. Only the url token is missing.
 * @param token (Token) Token related parameters.
 */
@ConfigurationProperties("url-shortener")
@Validated
public record UrlShortenerProperties (@NotBlank String baseUrl, @NotNull Token token) {

	/**
	 *
	 * @param length (int) Length of an url token. This is the size of short url token.
	 * @param maxAttempts (int) Maximum number of attempts to create an url token.
	 * @param characters (String) Possible characters for an url token. These are all the available characters that can be used to create a short url token.
	 */
	public record Token(@NotNull @Positive Integer length, @NotNull @Positive Integer maxAttempts, @NotBlank String characters) {}

}
