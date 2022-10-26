package com.project.urlshortener.controller;

import com.project.urlshortener.common.model.RestBasicResponse;
import com.project.urlshortener.exception.ShortUrlInvalidUrlException;
import com.project.urlshortener.model.api.createshorturl.UrlShortenerCreateShortUrlRequest;
import com.project.urlshortener.model.api.createshorturl.UrlShortenerCreateShortUrlResponse;
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

public class CreateShortUrlControllerTest {

	void setUpMessageSourceForExceptions(final MessageSource mockMessageSource) {
		doAnswer(invocationOnMock -> {
			String key = invocationOnMock.getArgument(0);
			Object[] params = invocationOnMock.getArgument(1);
			Locale locale = invocationOnMock.getArgument(2);

			return String.format("%s-%s-param=[%s]", key, locale.toString(), (params != null ? params[0] : null));

		}).when(mockMessageSource).getMessage(anyString(), any(), any());
	}

	@Nested
	@DisplayName("CreateShortUrlController.createShortUrl tests")
	class CreateShortUrlTest {

		@Mock
		private UrlShortenerService mockUrlShortenerService;

		@Mock
		private MessageSource mockMessageSource;

		@InjectMocks
		private CreateShortUrlController createShortUrlController;

		private UrlShortenerCreateShortUrlRequest parameterUrlShortenerCreateShortUrlRequest;
		private ResponseEntity<UrlShortenerCreateShortUrlResponse> resultCreateShortUrlResponse;
		private Exception caughtException;

		@BeforeEach
		void setUp() {
			MockitoAnnotations.openMocks(this);
			parameterUrlShortenerCreateShortUrlRequest = null;
			resultCreateShortUrlResponse = null;
			caughtException = null;
		}

		@Test
		@DisplayName("createShortUrl : when originalUrl is passed as a parameter and service.createShortUrlTokenForOriginalUrl returns the short url, then service.getOriginalUrlForShortUrlToken is called and short url is returned")
		void createShortUrl_shortUrlCreated() throws ShortUrlInvalidUrlException {
			when(mockUrlShortenerService.obtainShortUrlForOriginalCompleteUrl("http://myurl")).thenReturn("http://shorturl/0123456789");


			given_createShortUrlRequest(UrlShortenerCreateShortUrlRequest.builder().url("http://myurl").build());

			when_createShortUrl();

			then_urlShortenerServiceCreateShortUrlTokenForOriginalUrlWasCalled("http://myurl");
			then_noException();
			then_resultShortUrlIs("http://shorturl/0123456789");
		}

		@Test
		@DisplayName("createShortUrl : when originalUrl is passed as a parameter but service.createShortUrlTokenForOriginalUrl throws ShortUrlInvalidUrlException, then service.getOriginalUrlForShortUrlToken is called and ShortUrlInvalidUrlException is thrown")
		void createShortUrl_error_invalidUrl() throws ShortUrlInvalidUrlException {
			when(mockUrlShortenerService.obtainShortUrlForOriginalCompleteUrl("httpmyurl")).thenThrow(new ShortUrlInvalidUrlException("httpmyurl"));


			given_createShortUrlRequest(UrlShortenerCreateShortUrlRequest.builder().url("httpmyurl").build());

			when_createShortUrl();

			then_urlShortenerServiceCreateShortUrlTokenForOriginalUrlWasCalled("httpmyurl");
			then_exceptionThrown(ShortUrlInvalidUrlException.class, "[url=httpmyurl]");
		}



		private void given_createShortUrlRequest(UrlShortenerCreateShortUrlRequest request) {
			this.parameterUrlShortenerCreateShortUrlRequest = request;
		}

		private void when_createShortUrl() {
			try {
				resultCreateShortUrlResponse = createShortUrlController.createShortUrl(parameterUrlShortenerCreateShortUrlRequest);
			} catch(Exception e) {
				caughtException = e;
			}
		}

		private void then_urlShortenerServiceCreateShortUrlTokenForOriginalUrlWasCalled(String token) throws ShortUrlInvalidUrlException {
			verify(mockUrlShortenerService, times(1)).obtainShortUrlForOriginalCompleteUrl(token);
		}

		private void then_exceptionThrown(final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
			assertException(caughtException, expectedException, expectedExceptionMessage);
		}

		private void then_noException() {
			assertNoException(caughtException);
		}

		private void then_resultShortUrlIs(String shortUrl) {
			assertNotNull(resultCreateShortUrlResponse);
			assertEquals(HttpStatus.OK, resultCreateShortUrlResponse.getStatusCode());
			assertNotNull(resultCreateShortUrlResponse.getBody());
			assertNotNull(resultCreateShortUrlResponse.getBody().getShortUrl());
			assertEquals(shortUrl, resultCreateShortUrlResponse.getBody().getShortUrl());
		}
	}


	@Nested
	@DisplayName("CreateShortUrlController.onShortUrlInvalidUrlException tests")
	class OnShortUrlInvalidUrlExceptionTest {

		@Mock
		private UrlShortenerService mockUrlShortenerService;

		@Mock
		private MessageSource mockMessageSource;

		@InjectMocks
		private CreateShortUrlController createShortUrlController;

		private ShortUrlInvalidUrlException parameterShortUrlInvalidUrlException;
		private RestBasicResponse resultRestBasicResponse;

		@BeforeEach
		void setUp() {
			MockitoAnnotations.openMocks(this);
			parameterShortUrlInvalidUrlException = null;
			resultRestBasicResponse = null;
			setUpMessageSourceForExceptions(mockMessageSource);
		}

		@Test
		@DisplayName("onShortUrlInvalidUrlException : should call message source with the message key from inside ShortUrlInvalidUrlException, with the active English locale, and with the token value as a parameter")
		void onShortUrlInvalidUrlException_localeEnglish() {
			given_locale(Locale.ENGLISH);
			given_shortUrlInvalidUrlException(new ShortUrlInvalidUrlException("url-value"));

			when_onShortUrlInvalidUrlException();

			then_resultRestBasicResponseEquals(RestBasicResponse.builder().success(false).error("error.shorturl.InvalidUrl-en-param=[url-value]").build());
			then_messageSourceWasCalled("error.shorturl.InvalidUrl", new String[] { "url-value" }, Locale.ENGLISH);
		}

		@Test
		@DisplayName("onShortUrlInvalidUrlException : should call message source with the message key from inside ShortUrlInvalidUrlException, with the active French locale, and with the token value as a parameter")
		void onShortUrlInvalidUrlException_localeFrench() {
			given_locale(Locale.FRENCH);
			given_shortUrlInvalidUrlException(new ShortUrlInvalidUrlException("url-value"));

			when_onShortUrlInvalidUrlException();

			then_resultRestBasicResponseEquals(RestBasicResponse.builder().success(false).error("error.shorturl.InvalidUrl-fr-param=[url-value]").build());
			then_messageSourceWasCalled("error.shorturl.InvalidUrl", new String[] { "url-value" }, Locale.FRENCH);
		}

		private void given_locale(final Locale locale) {
			LocaleContextHolder.setDefaultLocale(locale);
		}

		private void given_shortUrlInvalidUrlException(final ShortUrlInvalidUrlException exception) {
			this.parameterShortUrlInvalidUrlException = exception;
		}

		private void when_onShortUrlInvalidUrlException() {
			resultRestBasicResponse = createShortUrlController.onShortUrlInvalidUrlException(parameterShortUrlInvalidUrlException);
		}

		private void then_resultRestBasicResponseEquals(final RestBasicResponse expectedReponse) {
			assertEquals(expectedReponse, resultRestBasicResponse);
		}

		private void then_messageSourceWasCalled(final String key, final Object[] params, final Locale locale) {
			verify(mockMessageSource, times(1)).getMessage(key, params, locale);
		}

	}


}
