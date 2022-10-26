package com.project.urlshortener.service;

import com.project.urlshortener.exception.*;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.model.properties.UrlShortenerProperties;
import com.project.urlshortener.repository.ShortUrlDao;
import com.project.urlshortener.service.impl.UrlShortenerServiceImpl;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.project.urlshortener.utils.AssertionUtils.assertException;
import static com.project.urlshortener.utils.AssertionUtils.assertNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UrlShortenerServiceTest {

    @Nested
    @DisplayName("UrlShortenerService.obtainShortUrlForOriginalCompleteUrl tests")
    class ObtainShortUrlForOriginalCompleteUrlTest {

        private String parametersOriginalUrl;
        private String resultShortUrl;
        private Exception caughtException;

        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        @Mock
        private ShortUrlDao mockShortUrlDao;
        @Mock
        private UrlValidator mockUrlValidator;
        private UrlShortenerService service;

        private static final String BASE_URL = "http://junit/";

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parametersOriginalUrl = null;
            resultShortUrl = null;
            caughtException = null;
            service = new UrlShortenerServiceImpl(mockUrlShortenerProperties, mockShortUrlDao, mockUrlValidator);
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn(BASE_URL);
            when(mockUrlShortenerProperties.getUrlshortenerTokenCharacters()).thenReturn("abcdefghijklmnopqrstuvwxyz");
            when(mockUrlShortenerProperties.getUrlshortenerTokenLength()).thenReturn(10);
        }

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url does not already exist, then createNewShortUrlEntityRetryable is called and token and baseurl are combined to create the shorturl")
        void obtainShortUrlForOriginalCompleteUrl_shouldCombineBaseUrlAndNewlyCreatedTokenToCreateShortUrl() {
            when(mockUrlValidator.isValid("http://originalurl")).thenReturn(true);
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://originalurl")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://originalurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcdef").build());

            given_originalUrl("http://originalurl");

            when_obtainShortUrlForOriginalCompleteUrl();

            then_noException();
            then_resultShortUrlEquals(BASE_URL + "abcdef");
            then_mockUrlValidatorIsValidIsCalled("http://originalurl");
            then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsCalled("http://originalurl");
            then_shortUrlDaoCreateNewShortUrlEntityRetryableIsCalled("http://originalurl");
        }

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url already exists for an url, then createNewShortUrlEntityRetryable is not called and already existing token and baseurl are combined to create the shorturl")
        void obtainShortUrlForOriginalCompleteUrl_tokenAlreadyExists() {
            when(mockUrlValidator.isValid("http://originalurl")).thenReturn(true);
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://originalurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcdef000").build());
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://originalurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcdef").build());

            given_originalUrl("http://originalurl");

            when_obtainShortUrlForOriginalCompleteUrl();

            then_noException();
            then_resultShortUrlEquals(BASE_URL + "abcdef000");
            then_mockUrlValidatorIsValidIsCalled("http://originalurl");
            then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsCalled("http://originalurl");
            then_shortUrlDaoCreateNewShortUrlEntityRetryableIsNotCalled();
        }

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url does not already exist but createNewShortUrlEntityRetryable fails, then ShortUrlInvalidUrlException")
        void obtainShortUrlForOriginalCompleteUrl_error_urlIsInvalid() {
            when(mockUrlValidator.isValid("http:originalurl")).thenReturn(false);

            given_originalUrl("http:originalurl");

            when_obtainShortUrlForOriginalCompleteUrl();

            then_exceptionThrown(ShortUrlInvalidUrlException.class, "[url=http:originalurl]");
            then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsNotCalled();
            then_shortUrlDaoCreateNewShortUrlEntityRetryableIsNotCalled();
        }

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url does not already exist but createNewShortUrlEntityRetryable fails, then ShortUrlTokenCannotBeCreatedException")
        void obtainShortUrlForOriginalCompleteUrl_error_newTokenFailure() {
            when(mockUrlValidator.isValid("http://originalurl")).thenReturn(true);
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://originalurl")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://originalurl")).thenThrow(new ShortUrlTokenAlreadyUsedException("error", "http://originalurl"));

            given_originalUrl("http://originalurl");

            when_obtainShortUrlForOriginalCompleteUrl();

            then_exceptionThrown(ShortUrlTokenCannotBeCreatedException.class, "[originalUrl=http://originalurl]");
        }

        private void given_originalUrl(final String url) {
            this.parametersOriginalUrl = url;
        }

        private void when_obtainShortUrlForOriginalCompleteUrl() {
            try {
                resultShortUrl = service.obtainShortUrlForOriginalCompleteUrl(parametersOriginalUrl);
            } catch(Exception e) {
                caughtException = e;
            }
        }

        private void then_exceptionThrown(final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
            assertException(caughtException, expectedException, expectedExceptionMessage);
        }

        private void then_noException() {
            assertNoException(caughtException);
        }

        private void then_resultShortUrlEquals(final String expectedShortUrl) {
            then_noException();
            assertNotNull(resultShortUrl);

            assertEquals(expectedShortUrl, resultShortUrl);
        }

        private void then_mockUrlValidatorIsValidIsCalled(final String originalUrl) {
            verify(mockUrlValidator, times(1)).isValid(originalUrl);
        }

        private void then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsCalled(final String originalUrl) {
            verify(mockShortUrlDao, times(1)).findExistingShortUrlEntityByOriginalUrl(originalUrl);
        }

        private void then_shortUrlDaoCreateNewShortUrlEntityRetryableIsCalled(final String originalUrl) {
            verify(mockShortUrlDao, times(1)).createNewShortUrlEntityRetryable(originalUrl);
        }

        private void then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsNotCalled() {
            verify(mockShortUrlDao, times(0)).findExistingShortUrlEntityByOriginalUrl(anyString());
        }

        private void then_shortUrlDaoCreateNewShortUrlEntityRetryableIsNotCalled() {
            verify(mockShortUrlDao, times(0)).createNewShortUrlEntityRetryable(anyString());
        }

    }


    @Nested
    @DisplayName("UrlShortenerService.getOriginalUrlForShortUrlToken tests")
    class GetOriginalUrlForShortUrlTokenTest {

        private String parametersShortUrlToken;
        private String resultOriginalUrl;
        private Exception caughtException;

        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        @Mock
        private ShortUrlDao mockShortUrlDao;
        @Mock
        private UrlValidator mockUrlValidator;
        private UrlShortenerService service;

        private static final String BASE_URL = "http://junit/";

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parametersShortUrlToken = null;
            resultOriginalUrl = null;
            caughtException = null;
            service = new UrlShortenerServiceImpl(mockUrlShortenerProperties, mockShortUrlDao, mockUrlValidator);
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn(BASE_URL);
            when(mockUrlShortenerProperties.getUrlshortenerTokenCharacters()).thenReturn("abcdefghijklmnopqrstuvwxyz");
            when(mockUrlShortenerProperties.getUrlshortenerTokenLength()).thenReturn(10);
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url already exists, then findExistingShortUrlEntityByToken is called and original url is returned")
        void getOriginalUrlForShortUrlToken_shouldCombineBaseUrlAndNewlyCreatedTokenToCreateShortUrl() {
            when(mockShortUrlDao.findExistingShortUrlEntityByToken("abcdef")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcdef").build());

            given_shortUrlToken("abcdef");

            when_getOriginalUrlForShortUrlToken();

            then_noException();
            then_resultOriginalUrlEquals("http://originalurl");
            then_shortUrlDaoFindExistingShortUrlEntityByTokenIsCalled("abcdef");
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url token is null, then findExistingShortUrlEntityByToken is not called and ShortUrlInvalidTokenException is thrown")
        void getOriginalUrlForShortUrlToken_error_tokenIsNull() {
            given_shortUrlToken(null);

            when_getOriginalUrlForShortUrlToken();

            then_exceptionThrown(ShortUrlInvalidTokenException.class, "[token=<null>]");
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url token is empty, then findExistingShortUrlEntityByToken is not called and ShortUrlInvalidTokenException is thrown")
        void getOriginalUrlForShortUrlToken_error_tokenIsEmpty() {
            given_shortUrlToken("");

            when_getOriginalUrlForShortUrlToken();

            then_exceptionThrown(ShortUrlInvalidTokenException.class, "[token=]");
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url token is blank, then findExistingShortUrlEntityByToken is not called and ShortUrlInvalidTokenException is thrown")
        void getOriginalUrlForShortUrlToken_error_tokenIsBlank() {
            given_shortUrlToken("   ");

            when_getOriginalUrlForShortUrlToken();

            then_exceptionThrown(ShortUrlInvalidTokenException.class, "[token=   ]");
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url does not already exist, then findExistingShortUrlEntityByToken is called but ShortUrlTokenNotFoundException is thrown")
        void getOriginalUrlForShortUrlToken_error_shortUrlDoesNotExist() {
            when(mockShortUrlDao.findExistingShortUrlEntityByToken("abcdef")).thenReturn(null);

            given_shortUrlToken("abcdef");

            when_getOriginalUrlForShortUrlToken();

            then_exceptionThrown(ShortUrlTokenNotFoundException.class, "[token=abcdef]");
        }

        private void given_shortUrlToken(final String shortUrlToken) {
            this.parametersShortUrlToken = shortUrlToken;
        }

        private void when_getOriginalUrlForShortUrlToken() {
            try {
                resultOriginalUrl = service.getOriginalUrlForShortUrlToken(parametersShortUrlToken);
            } catch(Exception e) {
                caughtException = e;
            }
        }

        private void then_exceptionThrown(final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
            assertException(caughtException, expectedException, expectedExceptionMessage);
        }

        private void then_noException() {
            assertNoException(caughtException);
        }

        private void then_resultOriginalUrlEquals(final String expectedUrl) {
            then_noException();
            assertNotNull(resultOriginalUrl);

            assertEquals(expectedUrl, resultOriginalUrl);
        }

        private void then_shortUrlDaoFindExistingShortUrlEntityByTokenIsCalled(final String token) {
            verify(mockShortUrlDao, times(1)).findExistingShortUrlEntityByToken(token);
        }

    }
}
