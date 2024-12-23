package com.project.urlshortener.repository.impl;

import com.project.urlshortener.common.exception.RequiredValueException;
import com.project.urlshortener.configuration.properties.UrlShortenerProperties;
import com.project.urlshortener.exception.ShortUrlTokenAlreadyUsedException;
import com.project.urlshortener.exception.ShortUrlTokenCannotBeCreatedException;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.service.StringTokenService;
import com.project.urlshortener.utils.UrlShortenerPropertiesBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static com.project.urlshortener.common.exception.RequiredValueException.RequirementType.*;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShortUrlDaoImplTest {

    private UrlShortenerProperties urlShortenerProperties;
    private static final ShortUrlEntity SHORT_URL_ABCD = ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcd").build();

    @Mock
    private ShortUrlRepository mockUrlTokensRepository;
    @Mock
    private StringTokenService mockStringTokenService;

    @InjectMocks
    private ShortUrlDaoImpl shortUrlDaoImpl;

    @BeforeEach
    void setUp() {
        urlShortenerProperties = new UrlShortenerPropertiesBuilder().buildSpy();
        ReflectionTestUtils.setField(shortUrlDaoImpl, "urlShortenerProperties", urlShortenerProperties);
    }

    @Nested
    @DisplayName("ShortUrlDao.findExistingShortUrlEntityByToken tests")
    class FindExistingShortUrlEntityByTokenTest {

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is passed as a parameter, then repository.findByToken should be called and should return result from repository")
        void findExistingShortUrlEntityByToken_shouldCallRepositoryFindByToken() {
            // ---- GIVEN ----
            when(mockUrlTokensRepository.findByToken("abcd")).thenReturn(SHORT_URL_ABCD);

            // ---- WHEN ----
            var result = shortUrlDaoImpl.findExistingShortUrlEntityByToken("abcd");

            // ---- THEN ----
            verify(mockUrlTokensRepository, times(1)).findByToken("abcd");
            assertThat(result).isEqualTo(SHORT_URL_ABCD);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("findExistingShortUrlEntityByToken : when token is null or empty, then RequiredValueException")
        void findExistingShortUrlEntityByToken_error_nullToken(final String nullOrEmpty) {
            assertThatThrownBy(() -> shortUrlDaoImpl.findExistingShortUrlEntityByToken(nullOrEmpty))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "token")
                    .hasFieldOrPropertyWithValue("requirementType", isNull(nullOrEmpty) ? CANNOT_BE_NULL : CANNOT_BE_EMPTY);
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is blank, then RequiredValueException")
        void findExistingShortUrlEntityByToken_error_blankToken() {
            assertThatThrownBy(() -> shortUrlDaoImpl.findExistingShortUrlEntityByToken(StringUtils.SPACE))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "token")
                    .hasFieldOrPropertyWithValue("requirementType", CANNOT_BE_BLANK);
        }

    }

    @Nested
    @DisplayName("ShortUrlDao.findExistingShortUrlEntityByOriginalUrl tests")
    class FindExistingShortUrlEntityByOriginalUrlTest {

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is passed as a parameter, then repository.findByToken should be called and should return result from repository")
        void findExistingShortUrlEntityByOriginalUrl_shouldCallRepositoryFindByToken() {
            // ---- GIVEN ----
            when(mockUrlTokensRepository.findByOriginalUrl("http://originalurl")).thenReturn(SHORT_URL_ABCD);

            // ---- WHEN ----
            var result = shortUrlDaoImpl.findExistingShortUrlEntityByOriginalUrl("http://originalurl");

            // ---- THEN ----
            verify(mockUrlTokensRepository).findByOriginalUrl("http://originalurl");
            assertThat(result).isEqualTo(SHORT_URL_ABCD);
        }

        @ParameterizedTest
        @DisplayName("findExistingShortUrlEntityByToken : when originalUrl is null, then RequiredValueException")
        @NullAndEmptySource
        void findExistingShortUrlEntityByOriginalUrl_error_nullToken(final String nullOrEmpty) {
            assertThatThrownBy(() -> shortUrlDaoImpl.findExistingShortUrlEntityByOriginalUrl(nullOrEmpty))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "originalUrl")
                    .hasFieldOrPropertyWithValue("requirementType", isNull(nullOrEmpty) ? CANNOT_BE_NULL : CANNOT_BE_EMPTY);
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when originalUrl is blank, then RequiredValueException")
        void findExistingShortUrlEntityByOriginalUrl_error_blankToken() {
            assertThatThrownBy(() -> shortUrlDaoImpl.findExistingShortUrlEntityByOriginalUrl(StringUtils.SPACE))
                    .isInstanceOf(RequiredValueException.class)
                    .hasFieldOrPropertyWithValue("fieldName", "originalUrl")
                    .hasFieldOrPropertyWithValue("requirementType", CANNOT_BE_BLANK);
        }

    }



    @Nested
    @DisplayName("ShortUrlDao.createNewShortUrlEntityRetryable tests")
    class CreateNewShortUrlEntityRetryableTest {

        @BeforeEach
        void setUp() {
            urlShortenerProperties = new UrlShortenerPropertiesBuilder().withTokenCharacters("abcd").withTokenLength(10).buildSpy();
            ReflectionTestUtils.setField(shortUrlDaoImpl, "urlShortenerProperties", urlShortenerProperties);
        }

        @Test
        @DisplayName("createNewShortUrlEntityRetryable : when token is passed as a parameter, then repository.findByToken should be called and should return result from repository")
        void createNewShortUrlEntityRetryable_shouldCallRepositoryFindByTokenAndShouldCallRepositorySave() {
            // ---- GIVEN ----
            using_mocked_urlTokensRepository_save();
            when(mockStringTokenService.createStringToken(anyString(), anyInt())).thenReturn("MY_TOKEN");

            // ---- WHEN ----
            var result = shortUrlDaoImpl.createNewShortUrlEntityRetryable("http://originalurl");

            // ---- THEN ----
            verify(mockUrlTokensRepository).findByToken("MY_TOKEN");

            var entityCaptor = ArgumentCaptor.forClass(ShortUrlEntity.class);
            verify(mockUrlTokensRepository).save(entityCaptor.capture());
            var savedEntity = entityCaptor.getValue();
            assertThat(savedEntity).isNotNull()
                    .extracting("id", "originalUrl", "token")
                    .containsExactly(null, "http://originalurl", "MY_TOKEN");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getOriginalUrl()).isEqualTo(savedEntity.getOriginalUrl());
            assertThat(result.getToken()).isEqualTo(savedEntity.getToken());
        }

        @Test
        @DisplayName("createNewShortUrlEntityRetryable : when token is passed as a parameter but createStringToken returns null, then ShortUrlTokenCannotBeCreatedException")
        void createNewShortUrlEntityRetryable_error_createStringTokenReturnsNull() {
            when(mockStringTokenService.createStringToken(anyString(), anyInt())).thenReturn(null);

            assertThatThrownBy(() -> shortUrlDaoImpl.createNewShortUrlEntityRetryable("http://originalurl-fail"))
                    .isInstanceOf(ShortUrlTokenCannotBeCreatedException.class)
                    .hasFieldOrPropertyWithValue("originalUrl", "http://originalurl-fail");
        }

        @Test
        @DisplayName("createNewShortUrlEntityRetryable : when token is passed as a parameter but createStringToken returns a token that is already used, then ShortUrlTokenCannotBeCreatedException")
        void createNewShortUrlEntityRetryable_error_tokenAlreadyUsed() {
            when(mockStringTokenService.createStringToken(anyString(), anyInt())).thenReturn("TOKEN");
            when(mockUrlTokensRepository.findByToken("TOKEN")).thenReturn(ShortUrlEntity.builder().id(1L).token("TOKEN").originalUrl("alreadyUsed").build());

            assertThatThrownBy(() -> shortUrlDaoImpl.createNewShortUrlEntityRetryable("http://originalurl-fail-token"))
                    .isInstanceOf(ShortUrlTokenAlreadyUsedException.class)
                    .hasFieldOrPropertyWithValue("shortUrlToken", "TOKEN")
                    .hasFieldOrPropertyWithValue("originalUrl", "http://originalurl-fail-token");
        }

        private void using_mocked_urlTokensRepository_save() {
            doAnswer(invocationOnMock -> {
                ShortUrlEntity arg = invocationOnMock.getArgument(0);
                return ShortUrlEntity.builder().id(15L).originalUrl(arg.getOriginalUrl()).token(arg.getToken()).build();
            }).when(mockUrlTokensRepository).save(any(ShortUrlEntity.class));
        }

    }


    @Nested
    class GetMaxRetryableAttemptsTest {

        @Test
        void getMaxRetryableAttempts_shouldReadProperties() {
            // ---- WHEN ----
            var result = shortUrlDaoImpl.getMaxRetryableAttempts();

            // ---- THEN ----
            verify(urlShortenerProperties.token()).maxAttempts();
            assertThat(result).isEqualTo(urlShortenerProperties.token().maxAttempts());
        }
    }

}
