package com.project.urlshortener.controller;

import com.project.urlshortener.common.model.RestBasicResponse;
import com.project.urlshortener.exception.ShortUrlInvalidTokenException;
import com.project.urlshortener.exception.ShortUrlTokenNotFoundException;
import com.project.urlshortener.model.api.decodeshorturl.UrlShortenerDecodeShortUrlResponse;
import com.project.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

import static com.project.urlshortener.utils.AssertionUtils.assertException;
import static com.project.urlshortener.utils.AssertionUtils.assertNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class DecodeShortUrlControllerTest {

	void setUpMessageSourceForExceptions(final MessageSource mockMessageSource) {
		doAnswer(invocationOnMock -> {
			String key = invocationOnMock.getArgument(0);
			Object[] params = invocationOnMock.getArgument(1);
			Locale locale = invocationOnMock.getArgument(2);

			return String.format("%s-%s-param=[%s]", key, locale.toString(), (params != null ? params[0] : null));

		}).when(mockMessageSource).getMessage(anyString(), any(), any());
	}

	@Nested
	@DisplayName("DecodeShortUrlController.decodeShortUrl tests")
	class DecodeShortUrlTest {

		@Mock
		private UrlShortenerService mockUrlShortenerService;

		@Mock
		private MessageSource mockMessageSource;

		@InjectMocks
		private DecodeShortUrlController decodeShortUrlController;

		private String parameterShortUrlToken;
		private ResponseEntity<UrlShortenerDecodeShortUrlResponse> resultDecodeShortUrlResponse;
		private Exception caughtException;

		@BeforeEach
		void setUp() {
			MockitoAnnotations.openMocks(this);
			parameterShortUrlToken = null;
			resultDecodeShortUrlResponse = null;
			caughtException = null;
		}

		@Test
		@DisplayName("decodeShortUrl : when shortUrlToken is passed as a parameter and service.getOriginalUrlForShortUrlToken returns the original url, then service.getOriginalUrlForShortUrlToken is called and original url is returned")
		void decodeShortUrl_originalUrlFound() throws ShortUrlInvalidTokenException, ShortUrlTokenNotFoundException {
			when(mockUrlShortenerService.getOriginalUrlForShortUrlToken("abcde12345")).thenReturn("http://junit-url-for-token/");

			given_shortUrlToken("abcde12345");

			when_decodeShortUrl();

			then_urlShortenerServiceGetOriginalUrlForShortUrlTokenWasCalled("abcde12345");
			then_noException();
			then_resultOriginalUrlIs("http://junit-url-for-token/");
		}

		@Test
		@DisplayName("decodeShortUrl : when shortUrlToken is passed as a parameter but service.getOriginalUrlForShortUrlToken throws ShortUrlTokenNotFoundException, then service.getOriginalUrlForShortUrlToken is called and ShortUrlTokenNotFoundException is thrown")
		void decodeShortUrl_error_tokenNotFound() throws ShortUrlInvalidTokenException, ShortUrlTokenNotFoundException {
			when(mockUrlShortenerService.getOriginalUrlForShortUrlToken("abcde12345")).thenThrow(new ShortUrlTokenNotFoundException("abcde12345"));

			given_shortUrlToken("abcde12345");

			when_decodeShortUrl();

			then_urlShortenerServiceGetOriginalUrlForShortUrlTokenWasCalled("abcde12345");
			then_exceptionThrown(ShortUrlTokenNotFoundException.class, "[token=abcde12345]");
		}

		private void given_shortUrlToken(String token) {
			this.parameterShortUrlToken = token;
		}

		private void when_decodeShortUrl() {
			try {
				resultDecodeShortUrlResponse = decodeShortUrlController.decodeShortUrl(parameterShortUrlToken);
			} catch(Exception e) {
				caughtException = e;
			}
		}

		private void then_urlShortenerServiceGetOriginalUrlForShortUrlTokenWasCalled(String token) throws ShortUrlInvalidTokenException, ShortUrlTokenNotFoundException {
			verify(mockUrlShortenerService, times(1)).getOriginalUrlForShortUrlToken(token);
		}

		private void then_exceptionThrown(final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
			assertException(caughtException, expectedException, expectedExceptionMessage);
		}

		private void then_noException() {
			assertNoException(caughtException);
		}

		private void then_resultOriginalUrlIs(String url) {
			assertNotNull(resultDecodeShortUrlResponse);
			assertEquals(HttpStatus.OK, resultDecodeShortUrlResponse.getStatusCode());
			assertNotNull(resultDecodeShortUrlResponse.getBody());
			assertNotNull(resultDecodeShortUrlResponse.getBody().getOriginalCompleteUrl());
			assertEquals(url, resultDecodeShortUrlResponse.getBody().getOriginalCompleteUrl());
		}
	}


	@Nested
	@DisplayName("DecodeShortUrlController.onShortUrlInvalidTokenException tests")
	class OnShortUrlInvalidTokenExceptionTest {

		@Mock
		private UrlShortenerService mockUrlShortenerService;

		@Mock
		private MessageSource mockMessageSource;

		@InjectMocks
		private DecodeShortUrlController decodeShortUrlController;

		private ShortUrlInvalidTokenException parameterShortUrlInvalidTokenException;
		private RestBasicResponse resultRestBasicResponse;

		@BeforeEach
		void setUp() {
			MockitoAnnotations.openMocks(this);
			parameterShortUrlInvalidTokenException = null;
			resultRestBasicResponse = null;
			setUpMessageSourceForExceptions(mockMessageSource);
		}

		@Test
		@DisplayName("onShortUrlInvalidTokenException : should call message source with the message key from inside ShortUrlInvalidTokenException, with the active English locale, and with the token value as a parameter")
		void onShortUrlInvalidTokenException_localeEnglish() {
			given_locale(Locale.ENGLISH);
			given_shortUrlInvalidTokenException(new ShortUrlInvalidTokenException("token-value"));

			when_onShortUrlInvalidTokenException();

			then_resultRestBasicResponseEquals(RestBasicResponse.builder().success(false).error("error.shorturl.InvalidToken-en-param=[token-value]").build());
			then_messageSourceWasCalled("error.shorturl.InvalidToken", new String[] { "token-value" }, Locale.ENGLISH);
		}

		@Test
		@DisplayName("onShortUrlInvalidTokenException : should call message source with the message key from inside ShortUrlInvalidTokenException, with the active French locale, and with the token value as a parameter")
		void onShortUrlInvalidTokenException_localeFrench() {
			given_locale(Locale.FRENCH);
			given_shortUrlInvalidTokenException(new ShortUrlInvalidTokenException("token-value"));

			when_onShortUrlInvalidTokenException();

			then_resultRestBasicResponseEquals(RestBasicResponse.builder().success(false).error("error.shorturl.InvalidToken-fr-param=[token-value]").build());
			then_messageSourceWasCalled("error.shorturl.InvalidToken", new String[] { "token-value" }, Locale.FRENCH);
		}

		private void given_locale(final Locale locale) {
			LocaleContextHolder.setDefaultLocale(locale);
		}

		private void given_shortUrlInvalidTokenException(final ShortUrlInvalidTokenException exception) {
			this.parameterShortUrlInvalidTokenException = exception;
		}

		private void when_onShortUrlInvalidTokenException() {
			resultRestBasicResponse = decodeShortUrlController.onShortUrlInvalidTokenException(parameterShortUrlInvalidTokenException);
		}

		private void then_resultRestBasicResponseEquals(final RestBasicResponse expectedReponse) {
			assertEquals(expectedReponse, resultRestBasicResponse);
		}

		private void then_messageSourceWasCalled(final String key, final Object[] params, final Locale locale) {
			verify(mockMessageSource, times(1)).getMessage(key, params, locale);
		}

	}
}
