package com.project.urlshortener.service.impl;

import com.project.urlshortener.common.exception.RequiredValueException;
import com.project.urlshortener.exception.ShortUrlTokenCannotBeCreatedException;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.model.properties.UrlShortenerProperties;
import com.project.urlshortener.repository.ShortUrlDao;
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
import static org.mockito.Mockito.*;

public class UrlShortenerServiceImplTest {

    @Nested
    @DisplayName("UrlShortenerServiceImpl.findOrCreateShortUrlToken tests")
    class FindOrCreateShortUrlTokenTest {

        private String parametersOriginalUrl;
        private String resultShortUrlToken;
        private Exception caughtException;

        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        @Mock
        private ShortUrlDao mockShortUrlDao;
        @Mock
        private UrlValidator mockUrlValidator;
        private UrlShortenerServiceImpl service;

        private static final String BASE_URL = "http://junit/";

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parametersOriginalUrl = null;
            resultShortUrlToken = null;
            caughtException = null;
            service = new UrlShortenerServiceImpl(mockUrlShortenerProperties, mockShortUrlDao, mockUrlValidator);
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn(BASE_URL);
            when(mockUrlShortenerProperties.getUrlshortenerTokenCharacters()).thenReturn("abcdefghijklmnopqrstuvwxyz");
            when(mockUrlShortenerProperties.getUrlshortenerTokenLength()).thenReturn(10);


        }

        @Test
        @DisplayName("findOrCreateShortUrlToken : if short url already exists, then createNewShortUrlEntityRetryable is NOT called and old token is returned")
        void findOrCreateShortUrlToken_shortUrlAlreadyExists() {
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://originalurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("old-token").build());
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://originalurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("new-token").build());

            given_originalUrl("http://originalurl");

            when_findOrCreateShortUrlToken();

            then_noException();
            then_resultShortUrlTokenEquals("old-token");
            then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsCalled("http://originalurl");
            then_shortUrlDaoCreateNewShortUrlEntityRetryableIsNotCalled();
        }

        @Test
        @DisplayName("findOrCreateShortUrlToken : if short url does not already exist, then createNewShortUrlEntityRetryable is called and new token is returned")
        void findOrCreateShortUrlToken_shortUrlDoesntAlreadyExist() {
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://originalurl")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://originalurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("new-token").build());

            given_originalUrl("http://originalurl");

            when_findOrCreateShortUrlToken();

            then_noException();
            then_resultShortUrlTokenEquals("new-token");
            then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsCalled("http://originalurl");
            then_shortUrlDaoCreateNewShortUrlEntityRetryableIsCalled("http://originalurl");
        }

        @Test
        @DisplayName("findOrCreateShortUrlToken : if short url does not already exist, but createNewShortUrlEntityRetryable returns null, then ShortUrlTokenCannotBeCreatedException")
        void findOrCreateShortUrlToken_createNewShortUrlEntityRetryableReturnsNull() {
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://originalurl")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://originalurl")).thenReturn(null);

            given_originalUrl("http://originalurl");

            when_findOrCreateShortUrlToken();

            then_exceptionThrown(ShortUrlTokenCannotBeCreatedException.class, "[originalUrl=http://originalurl]");
        }

        private void given_originalUrl(final String url) {
            this.parametersOriginalUrl = url;
        }

        private void when_findOrCreateShortUrlToken() {
            try {
                resultShortUrlToken = service.findOrCreateShortUrlToken(parametersOriginalUrl);
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

        private void then_resultShortUrlTokenEquals(final String expectedShortUrlToken) {
            then_noException();
            assertNotNull(resultShortUrlToken);

            assertEquals(expectedShortUrlToken, resultShortUrlToken);
        }

        private void then_shortUrlDaoFindExistingShortUrlEntityByOriginalUrlIsCalled(final String originalUrl) {
            verify(mockShortUrlDao, times(1)).findExistingShortUrlEntityByOriginalUrl(originalUrl);
        }

        private void then_shortUrlDaoCreateNewShortUrlEntityRetryableIsCalled(final String originalUrl) {
            verify(mockShortUrlDao, times(1)).createNewShortUrlEntityRetryable(originalUrl);
        }

        private void then_shortUrlDaoCreateNewShortUrlEntityRetryableIsNotCalled() {
            verify(mockShortUrlDao, times(0)).createNewShortUrlEntityRetryable(anyString());
        }

    }


    @Nested
    @DisplayName("UrlShortenerServiceImpl.buildShortUrlForToken tests")
    class BuildShortUrlForTokenTest {

        private String parametersShortUrlToken;
        private String resultShortUrl;
        private Exception caughtException;

        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        @Mock
        private ShortUrlDao mockShortUrlDao;
        @Mock
        private UrlValidator mockUrlValidator;
        private UrlShortenerServiceImpl service;

        private static final String BASE_URL = "http://junit/";
        private static final String BASE_URL_WITHOUT_SLASH = "http://junit-without-slash";

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parametersShortUrlToken = null;
            resultShortUrl = null;
            caughtException = null;
            service = new UrlShortenerServiceImpl(mockUrlShortenerProperties, mockShortUrlDao, mockUrlValidator);
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn(BASE_URL);
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is passed as a parameter, baseUrl and token are combined and short url is returned")
        void buildShortUrlForToken_shortUrlAlreadyExists() {
            given_shortUrlToken("ABCDEF");

            when_buildShortUrlForToken();

            then_noException();
            then_resultShortUrlEquals("http://junit/ABCDEF");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is passed as a parameter, baseUrl and token are combined and short url is returned")
        void buildShortUrlForToken_baseUrlDoesntEndWithSlash() {
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn(BASE_URL_WITHOUT_SLASH);
            given_shortUrlToken("ABCDEF");

            when_buildShortUrlForToken();

            then_noException();
            then_resultShortUrlEquals("http://junit-without-slash/ABCDEF");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is null, then RequiredValueException")
        void buildShortUrlForToken_error_tokenIsNull() {
            given_shortUrlToken(null);

            when_buildShortUrlForToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=shortUrlToken,requirementType=CANNOT_BE_NULL]");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is empty, then RequiredValueException")
        void buildShortUrlForToken_error_tokenIsEmpty() {
            given_shortUrlToken("");

            when_buildShortUrlForToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=shortUrlToken,requirementType=CANNOT_BE_EMPTY]");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is blank, then RequiredValueException")
        void buildShortUrlForToken_error_tokenIsBlank() {
            given_shortUrlToken("   ");

            when_buildShortUrlForToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=shortUrlToken,requirementType=CANNOT_BE_BLANK]");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if baseUrl is null, then RequiredValueException")
        void buildShortUrlForToken_error_baseUrlIsNull() {
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn(null);
            given_shortUrlToken("token");

            when_buildShortUrlForToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=urlShortenerProperties.getUrlShortenerBaseUrl(),requirementType=CANNOT_BE_NULL]");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if baseUrl is empty, then RequiredValueException")
        void buildShortUrlForToken_error_baseUrlIsEmpty() {
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn("");
            given_shortUrlToken("token");

            when_buildShortUrlForToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=urlShortenerProperties.getUrlShortenerBaseUrl(),requirementType=CANNOT_BE_EMPTY]");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if baseUrl is blank, then RequiredValueException")
        void buildShortUrlForToken_error_baseUrlIsBlank() {
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn("   ");
            given_shortUrlToken("token");

            when_buildShortUrlForToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=urlShortenerProperties.getUrlShortenerBaseUrl(),requirementType=CANNOT_BE_BLANK]");
        }

        private void given_shortUrlToken(final String urlToken) {
            this.parametersShortUrlToken = urlToken;
        }

        private void when_buildShortUrlForToken() {
            try {
                resultShortUrl = service.buildShortUrlForToken(parametersShortUrlToken);
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
    }


    @Nested
    @DisplayName("UrlShortenerServiceImpl.isUrlValid tests")
    class IsUrlValidTest {

        private String parameterUrl;
        private boolean resultUrlValid;
        private Exception caughtException;

        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        @Mock
        private ShortUrlDao mockShortUrlDao;
        @Mock
        private UrlValidator mockUrlValidator;
        private UrlShortenerServiceImpl service;

        private static final String BASE_URL = "http://junit/";

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parameterUrl = null;
            resultUrlValid = false;
            caughtException = null;
            service = new UrlShortenerServiceImpl(mockUrlShortenerProperties, mockShortUrlDao, mockUrlValidator);
            when(mockUrlShortenerProperties.getUrlShortenerBaseUrl()).thenReturn(BASE_URL);
        }

        @Test
        @DisplayName("isUrlValid : if urlValidator returns true, then returns true")
        void isUrlValid_urlIsValid() {
            when(mockUrlValidator.isValid("http://testurl")).thenReturn(true);
            given_url("http://testurl");

            when_isUrlValid();

            then_noException();
            then_mockUrlValidatorIsValidIsCalled("http://testurl");
            then_resultUrlValidEquals(true);
        }

        @Test
        @DisplayName("isUrlValid : if urlValidator returns false, then returns false")
        void isUrlValid_urlIsNotValid() {
            when(mockUrlValidator.isValid("http://testurl")).thenReturn(false);
            given_url("http://testurl");

            when_isUrlValid();

            then_noException();
            then_mockUrlValidatorIsValidIsCalled("http://testurl");
            then_resultUrlValidEquals(false);
        }

        private void given_url(final String url) {
            this.parameterUrl = url;
        }

        private void when_isUrlValid() {
            try {
                resultUrlValid = service.isUrlValid(parameterUrl);
            } catch(Exception e) {
                caughtException = e;
            }
        }

        private void then_noException() {
            assertNoException(caughtException);
        }

        private void then_resultUrlValidEquals(final boolean expectedValid) {
            then_noException();
            assertEquals(expectedValid, resultUrlValid);
        }

        private void then_mockUrlValidatorIsValidIsCalled(final String originalUrl) {
            verify(mockUrlValidator, times(1)).isValid(originalUrl);
        }
    }


    @Nested
    @DisplayName("UrlShortenerServiceImpl.isTokenValid tests")
    class IsTokenValidTest {

        private String parameterToken;
        private boolean resultTokenValid;
        private Exception caughtException;

        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        @Mock
        private ShortUrlDao mockShortUrlDao;
        @Mock
        private UrlValidator mockUrlValidator;
        private UrlShortenerServiceImpl service;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parameterToken = null;
            resultTokenValid = false;
            caughtException = null;
            service = new UrlShortenerServiceImpl(mockUrlShortenerProperties, mockShortUrlDao, mockUrlValidator);
        }

        @Test
        @DisplayName("isTokenValid : if token is not null and not empty and not blank, then returns true")
        void isTokenValid_tokenIsValid() {
            given_token("abcdef");

            when_isTokenValid();

            then_noException();
            then_resultTokenValidEquals(true);
        }

        @Test
        @DisplayName("isTokenValid : if token is null, then returns false")
        void isTokenValid_tokenIsNull() {
            given_token(null);

            when_isTokenValid();

            then_noException();
            then_resultTokenValidEquals(false);
        }

        @Test
        @DisplayName("isTokenValid : if token is empty, then returns false")
        void isTokenValid_tokenIsEmpty() {
            given_token("");

            when_isTokenValid();

            then_noException();
            then_resultTokenValidEquals(false);
        }

        @Test
        @DisplayName("isTokenValid : if token is blank, then returns false")
        void isTokenValid_tokenIsBlank() {
            given_token("  ");

            when_isTokenValid();

            then_noException();
            then_resultTokenValidEquals(false);
        }

        private void given_token(final String token) {
            this.parameterToken = token;
        }

        private void when_isTokenValid() {
            try {
                resultTokenValid = service.isTokenValid(parameterToken);
            } catch(Exception e) {
                caughtException = e;
            }
        }

        private void then_noException() {
            assertNoException(caughtException);
        }

        private void then_resultTokenValidEquals(final boolean expectedValid) {
            then_noException();
            assertEquals(expectedValid, resultTokenValid);
        }

    }

}
