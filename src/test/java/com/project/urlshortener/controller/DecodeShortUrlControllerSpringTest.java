package com.project.urlshortener.controller;

import com.project.urlshortener.model.api.decodeshorturl.UrlShortenerDecodeShortUrlResponse;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.model.properties.UrlShortenerProperties;
import com.project.urlshortener.repository.ShortUrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DecodeShortUrlControllerSpringTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ShortUrlRepository shortUrlRepository;

	@Autowired
	private UrlShortenerProperties urlShortenerProperties;

	@Nested
	@DisplayName("GET / Spring tests")
	class GetDecodeShortUrlTest {

		private String parameterShortUrlToken;
		private String optionalParameterLanguageParam;
		private ResponseEntity<UrlShortenerDecodeShortUrlResponse> responseEntity;

		@BeforeEach
		void setUp() {
			this.parameterShortUrlToken = null;
			this.responseEntity = null;
		}

		@Test
		@DisplayName("GET / : when a url token already exists in the database, and the url token is passed as a parameter, then httpStatus is OK, the original url is returned")
		void decodeShortUrl_TokenFound() {
			given_alreadyExistingUrl("https://www.journaldemontreal.com/", "abcdeFGHIJ");
			given_shortUrlToken("abcdeFGHIJ");

			when_callEndpointDecodeShortUrl();

			then_responseHttpCodeIs(HttpStatus.OK);
			then_responseSuccessIs(true);
			then_responseOriginalUrlIs("https://www.journaldemontreal.com/");
			then_responseErrorIs(null);
		}

		@Test
		@DisplayName("GET / : when a url token (that does not exist) is passed as a parameter, then httpStatus is NOT FOUND, and error is error.shorturl.TokenNotFound")
		void decodeShortUrl_error_TokenNotFound() {
			given_shortUrlToken("0123456789");

			// default locale
			when_callEndpointDecodeShortUrl();

			then_responseHttpCodeIs(HttpStatus.NOT_FOUND);
			then_responseSuccessIs(false);
			then_responseOriginalUrlIs(null);
			then_responseErrorIs("No url could be found for the token[0123456789].");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("No url could be found for the token[0123456789].");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("Aucune url n'a \u00E9t\u00E9 trouv\u00E9e pour le jeton [0123456789].");
		}


		@Test
		@DisplayName("GET / : when a null token is passed as a parameter, then httpStatus is BAD REQUEST, and error is THE error.shorturl.InvalidToken")
		void decodeShortUrl_error_TokenNull() {
			given_shortUrlToken(null);

			// default locale
			when_callEndpointDecodeShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseErrorIs("The token [] is invalid.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("The token [] is invalid.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("Le jeton [] est invalide.");
		}

		@Test
		@DisplayName("GET / : when an empty token is passed as a parameter, then httpStatus is BAD REQUET, and error is error.shorturl.InvalidToken")
		void decodeShortUrl_error_TokenEmpty() {
			given_shortUrlToken("");

			// default locale
			when_callEndpointDecodeShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseErrorIs("The token [] is invalid.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("The token [] is invalid.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("Le jeton [] est invalide.");
		}

		@Test
		@DisplayName("GET / : when a blank token is passed as a parameter, then httpStatus is BAD REQUET, and error is error.shorturl.InvalidToken")
		void decodeShortUrl_error_TokenBlank() {
			given_shortUrlToken("    ");

			when_callEndpointDecodeShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseErrorIs("The token [    ] is invalid.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("The token [    ] is invalid.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointDecodeShortUrl();
			then_responseErrorIs("Le jeton [    ] est invalide.");
		}

		private void given_alreadyExistingUrl(final String originalUrl, final String shortUrlToken) {
			shortUrlRepository.save(ShortUrlEntity.builder().token(shortUrlToken).originalUrl(originalUrl).build());
		}

		private void given_shortUrlToken(final String shortUrlToken) {
			this.parameterShortUrlToken = shortUrlToken;
		}

		private void given_languageParam(final String langParam) {
			this.optionalParameterLanguageParam = langParam;
		}

		private void when_callEndpointDecodeShortUrl() {
			String endpoint = "/"
						+ (parameterShortUrlToken != null ? parameterShortUrlToken : StringUtils.EMPTY)
						+ (optionalParameterLanguageParam != null ? "?" + optionalParameterLanguageParam : StringUtils.EMPTY);

			responseEntity = restTemplate.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<>(){});
		}

		private void then_responseHttpCodeIs(final HttpStatus expectedHttpStatus) {
			assertNotNull(responseEntity);
			assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
		}

		private void then_responseOriginalUrlIs(final String url) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			if (url == null) {
				assertNull(responseEntity.getBody().getOriginalCompleteUrl());
			} else {
				assertEquals(url, responseEntity.getBody().getOriginalCompleteUrl());
			}
		}

		private void then_responseSuccessIs(final boolean expectedSuccess) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			assertEquals(expectedSuccess, responseEntity.getBody().isSuccess());
		}

		private void then_responseErrorIs(final String expectedError) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			if (expectedError == null) {
				assertNull(responseEntity.getBody().getError());
			} else {
				assertEquals(expectedError, responseEntity.getBody().getError());
			}
		}

	}


}
