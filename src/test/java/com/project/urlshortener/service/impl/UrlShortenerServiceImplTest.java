package com.project.urlshortener.service.impl;

import com.project.urlshortener.common.exception.RequiredValueException;
import com.project.urlshortener.configuration.properties.UrlShortenerProperties;
import com.project.urlshortener.exception.*;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.repository.ShortUrlDao;
import com.project.urlshortener.utils.UrlShortenerPropertiesBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;

import static com.project.urlshortener.common.exception.RequiredValueException.RequirementType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceImplTest {

    private static final String BASE_URL = "http://junit/";
    private static final String BASE_URL_WITHOUT_SLASH = "http://junit-without-slash";

    private UrlShortenerProperties urlShortenerProperties;

    @Mock
    private ShortUrlDao mockShortUrlDao;
    @Mock
    private UrlValidator mockUrlValidator;

    @InjectMocks
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setUp() {
        urlShortenerProperties = new UrlShortenerPropertiesBuilder()
                .withBaseUrl(BASE_URL)
                .buildSpy();
        ReflectionTestUtils.setField(service, "urlShortenerProperties", urlShortenerProperties);
    }


    @Nested
    @DisplayName("UrlShortenerService.obtainShortUrlForOriginalCompleteUrl tests")
    class ObtainShortUrlForOriginalCompleteUrlTest {

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url does not already exist, then createNewShortUrlEntityRetryable is called and token and baseurl are combined to create the shorturl")
        void obtainShortUrlForOriginalCompleteUrl_shouldCombineBaseUrlAndNewlyCreatedTokenToCreateShortUrl() throws ShortUrlInvalidUrlException {
            when(mockUrlValidator.isValid("http://testurl")).thenReturn(true);
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://testurl")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://testurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcdef").build());

            var resultShortUrl = service.obtainShortUrlForOriginalCompleteUrl("http://testurl");

            assertThat(resultShortUrl).isNotNull().isEqualTo(BASE_URL + "abcdef");
            verify(mockUrlValidator).isValid("http://testurl");
            verify(mockShortUrlDao).findExistingShortUrlEntityByOriginalUrl("http://testurl");
            verify(mockShortUrlDao).createNewShortUrlEntityRetryable("http://testurl");
        }

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url already exists for an url, then createNewShortUrlEntityRetryable is not called and already existing token and baseurl are combined to create the shorturl")
        void obtainShortUrlForOriginalCompleteUrl_tokenAlreadyExists() throws ShortUrlInvalidUrlException {
            when(mockUrlValidator.isValid("http://www.canada.ca/")).thenReturn(true);
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://www.canada.ca/")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcdef000").build());

            var resultShortUrl = service.obtainShortUrlForOriginalCompleteUrl("http://www.canada.ca/");

            assertThat(resultShortUrl).isNotNull().isEqualTo(BASE_URL + "abcdef000");
            verify(mockUrlValidator).isValid("http://www.canada.ca/");
            verify(mockShortUrlDao).findExistingShortUrlEntityByOriginalUrl("http://www.canada.ca/");
            verify(mockShortUrlDao, never()).createNewShortUrlEntityRetryable(anyString());
        }

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url does not already exist but createNewShortUrlEntityRetryable fails, then ShortUrlInvalidUrlException")
        void obtainShortUrlForOriginalCompleteUrl_error_urlIsInvalid() {
            when(mockUrlValidator.isValid("http:badurl")).thenReturn(false);

            assertThatThrownBy(() -> service.obtainShortUrlForOriginalCompleteUrl("http:badurl"))
                    .isInstanceOf(ShortUrlInvalidUrlException.class)
                            .hasFieldOrPropertyWithValue("url", "http:badurl");

            verify(mockShortUrlDao, never()).findExistingShortUrlEntityByOriginalUrl(anyString());
            verify(mockShortUrlDao, never()).createNewShortUrlEntityRetryable(anyString());
        }

        @Test
        @DisplayName("obtainShortUrlForOriginalCompleteUrl : if short url does not already exist but createNewShortUrlEntityRetryable fails, then ShortUrlTokenCannotBeCreatedException")
        void obtainShortUrlForOriginalCompleteUrl_error_newTokenFailure() {
            when(mockUrlValidator.isValid("http://www.google.com/")).thenReturn(true);
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://www.google.com/")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://www.google.com/"))
                    .thenThrow(new ShortUrlTokenAlreadyUsedException("error", "http://www.google.com/"));

            assertThatThrownBy(() -> service.obtainShortUrlForOriginalCompleteUrl("http://www.google.com/"))
                    .isInstanceOf(ShortUrlTokenCannotBeCreatedException.class)
                    .hasFieldOrPropertyWithValue("originalUrl", "http://www.google.com/");
        }


    }


    @Nested
    @DisplayName("UrlShortenerService.getOriginalUrlForShortUrlToken tests")
    class GetOriginalUrlForShortUrlTokenTest {

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url already exists, then findExistingShortUrlEntityByToken is called and original url is returned")
        void getOriginalUrlForShortUrlToken_shouldCombineBaseUrlAndNewlyCreatedTokenToCreateShortUrl() throws ShortUrlInvalidTokenException, ShortUrlTokenNotFoundException {
            when(mockShortUrlDao.findExistingShortUrlEntityByToken("abcdef")).thenReturn(ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcdef").build());

            var resultOriginalUrl = service.getOriginalUrlForShortUrlToken("abcdef");

            assertThat(resultOriginalUrl).isNotNull().isEqualTo("http://originalurl");
            verify(mockShortUrlDao).findExistingShortUrlEntityByToken("abcdef");
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url token is null, then findExistingShortUrlEntityByToken is not called and ShortUrlInvalidTokenException is thrown")
        void getOriginalUrlForShortUrlToken_error_tokenIsNull() {
            assertThatThrownBy(() -> service.getOriginalUrlForShortUrlToken(null))
                    .isInstanceOf(ShortUrlInvalidTokenException.class)
                    .hasFieldOrPropertyWithValue("token", null);
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url token is empty, then findExistingShortUrlEntityByToken is not called and ShortUrlInvalidTokenException is thrown")
        void getOriginalUrlForShortUrlToken_error_tokenIsEmpty() {
            assertThatThrownBy(() -> service.getOriginalUrlForShortUrlToken(StringUtils.EMPTY))
                    .isInstanceOf(ShortUrlInvalidTokenException.class)
                    .hasFieldOrPropertyWithValue("token", StringUtils.EMPTY);
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url token is blank, then findExistingShortUrlEntityByToken is not called and ShortUrlInvalidTokenException is thrown")
        void getOriginalUrlForShortUrlToken_error_tokenIsBlank() {
            assertThatThrownBy(() -> service.getOriginalUrlForShortUrlToken(StringUtils.SPACE))
                    .isInstanceOf(ShortUrlInvalidTokenException.class)
                    .hasFieldOrPropertyWithValue("token", StringUtils.SPACE);
        }

        @Test
        @DisplayName("getOriginalUrlForShortUrlToken : if short url does not already exist, then findExistingShortUrlEntityByToken is called but ShortUrlTokenNotFoundException is thrown")
        void getOriginalUrlForShortUrlToken_error_shortUrlDoesNotExist() {
            when(mockShortUrlDao.findExistingShortUrlEntityByToken("abcdef")).thenReturn(null);

            assertThatThrownBy(() -> service.getOriginalUrlForShortUrlToken("abcdef"))
                    .isInstanceOf(ShortUrlTokenNotFoundException.class)
                    .hasFieldOrPropertyWithValue("token", "abcdef");
        }

    }


    @Nested
    @DisplayName("UrlShortenerServiceImpl.findOrCreateShortUrlToken tests")
    class FindOrCreateShortUrlTokenTest {

        @Test
        @DisplayName("findOrCreateShortUrlToken : if short url already exists, then createNewShortUrlEntityRetryable is NOT called and old token is returned")
        void findOrCreateShortUrlToken_shortUrlAlreadyExists() {
            // ---- GIVEN ----
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://testurl")).thenReturn(ShortUrlEntity.builder().originalUrl("http://testurl").token("old-token").build());

            // ---- WHEN ----
            var result = service.findOrCreateShortUrlToken("http://testurl");

            // ---- THEN ----
            assertThat(result).isNotNull().isEqualTo("old-token");
            verify(mockShortUrlDao, times(1)).findExistingShortUrlEntityByOriginalUrl("http://testurl");
            verify(mockShortUrlDao, times(0)).createNewShortUrlEntityRetryable(anyString());
        }

        @Test
        @DisplayName("findOrCreateShortUrlToken : if short url does not already exist, then createNewShortUrlEntityRetryable is called and new token is returned")
        void findOrCreateShortUrlToken_shortUrlDoesntAlreadyExist() {
            // ---- GIVEN ----
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://my-url")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://my-url")).thenReturn(ShortUrlEntity.builder().originalUrl("http://my-url").token("new-token").build());

            // ---- WHEN ----
            var result = service.findOrCreateShortUrlToken("http://my-url");

            // ---- THEN ----
            assertThat(result).isNotNull().isEqualTo("new-token");
            verify(mockShortUrlDao, times(1)).findExistingShortUrlEntityByOriginalUrl("http://my-url");
            verify(mockShortUrlDao, times(1)).createNewShortUrlEntityRetryable("http://my-url");
        }

        @Test
        @DisplayName("findOrCreateShortUrlToken : if short url does not already exist, but createNewShortUrlEntityRetryable returns null, then ShortUrlTokenCannotBeCreatedException")
        void findOrCreateShortUrlToken_createNewShortUrlEntityRetryableReturnsNull() {
            // ---- GIVEN ----
            when(mockShortUrlDao.findExistingShortUrlEntityByOriginalUrl("http://junit-url")).thenReturn(null);
            when(mockShortUrlDao.createNewShortUrlEntityRetryable("http://junit-url")).thenReturn(null);

            // ---- WHEN ----
            assertThatThrownBy(() -> service.findOrCreateShortUrlToken("http://junit-url"))
                    .isInstanceOf(ShortUrlTokenCannotBeCreatedException.class)
                    .hasFieldOrPropertyWithValue("originalUrl", "http://junit-url");

        }

    }


    @Nested
    @DisplayName("UrlShortenerServiceImpl.buildShortUrlForToken tests")
    class BuildShortUrlForTokenTest {

        @BeforeEach
        void setup() {
            givenBaseUrl(BASE_URL);
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is passed as a parameter, baseUrl and token are combined and short url is returned")
        void buildShortUrlForToken_shortUrlAlreadyExists() {
            var result = service.buildShortUrlForToken("ABCDEF");

            assertThat(result).isNotNull().isEqualTo("http://junit/ABCDEF");
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is passed as a parameter, baseUrl and token are combined and short url is returned")
        void buildShortUrlForToken_baseUrlDoesntEndWithSlash() {
            givenBaseUrl(BASE_URL_WITHOUT_SLASH);

            var result = service.buildShortUrlForToken("ABCDEFGH");

            assertThat(result).isNotNull().isEqualTo("http://junit-without-slash/ABCDEFGH");
        }

        @ParameterizedTest
        @DisplayName("buildShortUrlForToken : if url token is null, then RequiredValueException")
        @NullAndEmptySource
        void buildShortUrlForToken_error_tokenIsNull(final String nullOrEmpty) {
            assertThatThrownBy(() -> service.buildShortUrlForToken(nullOrEmpty))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "shortUrlToken")
                    .hasFieldOrPropertyWithValue("requirementType",
                            Objects.isNull(nullOrEmpty) ? CANNOT_BE_NULL : CANNOT_BE_EMPTY);
        }

        @Test
        @DisplayName("buildShortUrlForToken : if url token is blank, then RequiredValueException")
        void buildShortUrlForToken_error_tokenIsBlank() {
            assertThatThrownBy(() -> service.buildShortUrlForToken(StringUtils.SPACE))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "shortUrlToken")
                    .hasFieldOrPropertyWithValue("requirementType", CANNOT_BE_BLANK);
        }

        @Test
        @DisplayName("buildShortUrlForToken : if baseUrl is null, then RequiredValueException")
        void buildShortUrlForToken_error_baseUrlIsNull() {
            givenBaseUrl(null);

            assertThatThrownBy(() -> service.buildShortUrlForToken("token"))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "urlShortenerProperties.baseUrl()")
                    .hasFieldOrPropertyWithValue("requirementType", CANNOT_BE_NULL);
        }

        @Test
        @DisplayName("buildShortUrlForToken : if baseUrl is empty, then RequiredValueException")
        void buildShortUrlForToken_error_baseUrlIsEmpty() {
            givenBaseUrl(StringUtils.EMPTY);

            assertThatThrownBy(() -> service.buildShortUrlForToken("mytoken"))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "urlShortenerProperties.baseUrl()")
                    .hasFieldOrPropertyWithValue("requirementType", CANNOT_BE_EMPTY);
        }

        @Test
        @DisplayName("buildShortUrlForToken : if baseUrl is blank, then RequiredValueException")
        void buildShortUrlForToken_error_baseUrlIsBlank() {
            givenBaseUrl(StringUtils.SPACE);

            assertThatThrownBy(() -> service.buildShortUrlForToken("mytoken"))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "urlShortenerProperties.baseUrl()")
                    .hasFieldOrPropertyWithValue("requirementType", CANNOT_BE_BLANK);
        }

    }


    @Nested
    @DisplayName("UrlShortenerServiceImpl.isUrlValid tests")
    class IsUrlValidTest {

        @Test
        @DisplayName("isUrlValid : if urlValidator returns true, then returns true")
        void isUrlValid_urlIsValid() {
            when(mockUrlValidator.isValid("http://testurl")).thenReturn(true);

            var resultUrlValid = service.isUrlValid("http://testurl");

            assertThat(resultUrlValid).isTrue();
            then_mockUrlValidatorIsValidIsCalled("http://testurl");
        }

        @Test
        @DisplayName("isUrlValid : if urlValidator returns false, then returns false")
        void isUrlValid_urlIsNotValid() {
            when(mockUrlValidator.isValid("http://juniturl")).thenReturn(false);

            var resultUrlValid = service.isUrlValid("http://juniturl");

            assertThat(resultUrlValid).isFalse();
            then_mockUrlValidatorIsValidIsCalled("http://juniturl");
        }

        private void then_mockUrlValidatorIsValidIsCalled(final String originalUrl) {
            verify(mockUrlValidator, times(1)).isValid(originalUrl);
        }
    }


    @Nested
    @DisplayName("UrlShortenerServiceImpl.isTokenValid tests")
    class IsTokenValidTest {

        @Test
        @DisplayName("isTokenValid : if token is not null and not empty and not blank, then returns true")
        void isTokenValid_tokenIsValid() {
            assertThat(service.isTokenValid("ABCDEFG")).isTrue();
        }

        @Test
        @DisplayName("isTokenValid : if token is null or empty or blank, then returns false")
        void isTokenValid_tokenIsNullOrEmptyOrBlank() {
            assertThat(service.isTokenValid(null)).isFalse();
            assertThat(service.isTokenValid(StringUtils.EMPTY)).isFalse();
            assertThat(service.isTokenValid(StringUtils.SPACE)).isFalse();
        }

    }

    private void givenBaseUrl(final String baseUrl) {
        urlShortenerProperties = new UrlShortenerPropertiesBuilder().withBaseUrl(baseUrl).buildSpy();
        ReflectionTestUtils.setField(service, "urlShortenerProperties", urlShortenerProperties);
    }
}
