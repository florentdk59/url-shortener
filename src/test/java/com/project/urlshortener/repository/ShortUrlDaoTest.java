package com.project.urlshortener.repository;

import com.project.urlshortener.common.exception.RequiredValueException;
import com.project.urlshortener.exception.ShortUrlTokenAlreadyUsedException;
import com.project.urlshortener.exception.ShortUrlTokenCannotBeCreatedException;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.model.properties.UrlShortenerProperties;
import com.project.urlshortener.repository.impl.ShortUrlDaoImpl;
import com.project.urlshortener.service.StringTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.project.urlshortener.utils.AssertionUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class ShortUrlDaoTest {

    @Nested
    @DisplayName("ShortUrlDao.findExistingShortUrlEntityByToken tests")
    class CreateStringTokenTest {
        private String parameterToken;
        private ShortUrlEntity resultShortUrlEntity;
        private Exception caughtException;

        @Mock
        private ShortUrlRepository mockUrlTokensRepository;
        @Mock
        private StringTokenService mockStringTokenService;
        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        private ShortUrlDao shortUrlDao;

        private static final ShortUrlEntity SHORT_URL_ABCD = ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcd").build();

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parameterToken = null;
            resultShortUrlEntity = null;
            caughtException = null;
            shortUrlDao = new ShortUrlDaoImpl(mockUrlTokensRepository, mockStringTokenService, mockUrlShortenerProperties);

            when(mockUrlTokensRepository.findByToken("abcd")).thenReturn(SHORT_URL_ABCD);
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is passed as a parameter, then repository.findByToken should be called and should return result from repository")
        void findExistingShortUrlEntityByToken_shouldCallRepositoryFindByToken() {
            given_token("abcd");

            when_findExistingShortUrlEntityByToken();

            then_noException();
            then_repositoryFindByTokenIsCalled("abcd");
            then_resultShortUrlEntityIs(SHORT_URL_ABCD);
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is null, then RequiredValueException")
        void findExistingShortUrlEntityByToken_error_nullToken() {
            given_token(null);

            when_findExistingShortUrlEntityByToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=token,requirementType=CANNOT_BE_NULL]");
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is empty, then RequiredValueException")
        void findExistingShortUrlEntityByToken_error_emptyToken() {
            given_token("");

            when_findExistingShortUrlEntityByToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=token,requirementType=CANNOT_BE_EMPTY]");
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is blank, then RequiredValueException")
        void findExistingShortUrlEntityByToken_error_blankToken() {
            given_token("   ");

            when_findExistingShortUrlEntityByToken();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=token,requirementType=CANNOT_BE_BLANK]");
        }

        private void given_token(final String token) {
            this.parameterToken = token;
        }

        private void when_findExistingShortUrlEntityByToken() {
            try {
                resultShortUrlEntity = shortUrlDao.findExistingShortUrlEntityByToken(parameterToken);
            } catch(Exception e) {
                caughtException = e;
            }
        }

        private void then_noException() {
            assertNoException(caughtException);
        }

        private void then_exceptionThrown(final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
            assertException(caughtException, expectedException, expectedExceptionMessage);
        }

        private void then_repositoryFindByTokenIsCalled(final String token) {
            then_noException();
            verify(mockUrlTokensRepository, times(1)).findByToken(token);
        }

        private void then_resultShortUrlEntityIs(final ShortUrlEntity expectedShortUrl) {
            then_noException();
            assertEquals(expectedShortUrl, resultShortUrlEntity);
        }
    }



    @Nested
    @DisplayName("ShortUrlDao.findExistingShortUrlEntityByOriginalUrl tests")
    class FindExistingShortUrlEntityByOriginalUrlTest {
        private String parameterOriginalUrl;
        private ShortUrlEntity resultShortUrlEntity;
        private Exception caughtException;

        @Mock
        private ShortUrlRepository mockUrlTokensRepository;
        @Mock
        private StringTokenService mockStringTokenService;
        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        private ShortUrlDao shortUrlDao;

        private static final ShortUrlEntity SHORT_URL_ABCD = ShortUrlEntity.builder().originalUrl("http://originalurl").token("abcd").build();

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parameterOriginalUrl = null;
            resultShortUrlEntity = null;
            caughtException = null;
            shortUrlDao = new ShortUrlDaoImpl(mockUrlTokensRepository, mockStringTokenService, mockUrlShortenerProperties);

            when(mockUrlTokensRepository.findByOriginalUrl("http://originalurl")).thenReturn(SHORT_URL_ABCD);
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is passed as a parameter, then repository.findByToken should be called and should return result from repository")
        void findExistingShortUrlEntityByOriginalUrl_shouldCallRepositoryFindByToken() {
            given_originalUrl("http://originalurl");

            when_findExistingShortUrlEntityByOriginalUrl();

            then_noException();
            then_repositoryFindByOriginalUrlIsCalled("http://originalurl");
            then_resultShortUrlEntityIs(SHORT_URL_ABCD);
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is null, then RequiredValueException")
        void findExistingShortUrlEntityByOriginalUrl_error_nullToken() {
            given_originalUrl(null);

            when_findExistingShortUrlEntityByOriginalUrl();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=originalUrl,requirementType=CANNOT_BE_NULL]");
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is empty, then RequiredValueException")
        void findExistingShortUrlEntityByOriginalUrl_error_emptyToken() {
            given_originalUrl("");

            when_findExistingShortUrlEntityByOriginalUrl();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=originalUrl,requirementType=CANNOT_BE_EMPTY]");
        }

        @Test
        @DisplayName("findExistingShortUrlEntityByToken : when token is blank, then RequiredValueException")
        void findExistingShortUrlEntityByOriginalUrl_error_blankToken() {
            given_originalUrl("   ");

            when_findExistingShortUrlEntityByOriginalUrl();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=originalUrl,requirementType=CANNOT_BE_BLANK]");
        }

        private void given_originalUrl(final String originalUrl) {
            this.parameterOriginalUrl = originalUrl;
        }

        private void when_findExistingShortUrlEntityByOriginalUrl() {
            try {
                resultShortUrlEntity = shortUrlDao.findExistingShortUrlEntityByOriginalUrl(parameterOriginalUrl);
            } catch(Exception e) {
                caughtException = e;
            }
        }

        private void then_noException() {
            assertNoException(caughtException);
        }

        private void then_exceptionThrown(final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
            if (caughtException == null) {
                fail(String.format("FAIL : expectedException[%s] but caughtException is null", expectedException));
            }
            assertEquals(expectedException, caughtException.getClass());
            assertStringContains(expectedExceptionMessage, caughtException.toString());
        }

        private void then_repositoryFindByOriginalUrlIsCalled(final String url) {
            then_noException();
            verify(mockUrlTokensRepository, times(1)).findByOriginalUrl(url);
        }

        private void then_resultShortUrlEntityIs(final ShortUrlEntity expectedShortUrl) {
            then_noException();
            assertEquals(expectedShortUrl, resultShortUrlEntity);
        }
    }



    @Nested
    @DisplayName("ShortUrlDao.createNewShortUrlEntityRetryable tests")
    class CreateNewShortUrlEntityRetryableTest {
        private String parameterOriginalUrl;
        private ShortUrlEntity resultShortUrlEntity;
        private Exception caughtException;

        @Mock
        private ShortUrlRepository mockUrlTokensRepository;
        @Mock
        private StringTokenService mockStringTokenService;
        @Mock
        private UrlShortenerProperties mockUrlShortenerProperties;
        private ShortUrlDao shortUrlDao;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            parameterOriginalUrl = null;
            resultShortUrlEntity = null;
            caughtException = null;
            shortUrlDao = new ShortUrlDaoImpl(mockUrlTokensRepository, mockStringTokenService, mockUrlShortenerProperties);

            doAnswer(invocationOnMock -> {
                ShortUrlEntity arg = invocationOnMock.getArgument(0);
                return ShortUrlEntity.builder().id(15L).originalUrl(arg.getOriginalUrl()).token(arg.getToken()).build();
            }).when(mockUrlTokensRepository).save(any(ShortUrlEntity.class));

            when(mockUrlShortenerProperties.getUrlshortenerTokenCharacters()).thenReturn("abcd");
            when(mockUrlShortenerProperties.getUrlshortenerTokenLength()).thenReturn(10);
        }

        @Test
        @DisplayName("createNewShortUrlEntityRetryable : when token is passed as a parameter, then repository.findByToken should be called and should return result from repository")
        void createNewShortUrlEntityRetryable_shouldCallRepositoryFindByTokenAndShouldCallRepositorySave() {
            when(mockStringTokenService.createStringToken(anyString(), anyInt())).thenReturn("TOKEN");
            given_originalUrl("http://originalurl");

            when_createNewShortUrlEntityRetryable();

            then_noException();
            then_repositoryFindByTokenIsCalled("TOKEN");
            then_repositorySaveIsCalled(ShortUrlEntity.builder().originalUrl("http://originalurl").token("TOKEN").build());
            then_resultShortUrlEntityIs(ShortUrlEntity.builder().id(15L).originalUrl("http://originalurl").token("TOKEN").build());
        }

        @Test
        @DisplayName("createNewShortUrlEntityRetryable : when token is passed as a parameter but createStringToken returns null, then ShortUrlTokenCannotBeCreatedException")
        void createNewShortUrlEntityRetryable_error_createStringTokenReturnsNull() {
            when(mockStringTokenService.createStringToken(anyString(), anyInt())).thenReturn(null);
            given_originalUrl("http://originalurl");

            when_createNewShortUrlEntityRetryable();

            then_exceptionThrown(ShortUrlTokenCannotBeCreatedException.class, "[originalUrl=http://originalurl]");
        }

        @Test
        @DisplayName("createNewShortUrlEntityRetryable : when token is passed as a parameter but createStringToken returns a token that is already used, then ShortUrlTokenCannotBeCreatedException")
        void createNewShortUrlEntityRetryable_error_tokenAlreadyUsed() {
            when(mockStringTokenService.createStringToken(anyString(), anyInt())).thenReturn("TOKEN");
            when(mockUrlTokensRepository.findByToken("TOKEN")).thenReturn(ShortUrlEntity.builder().id(1L).token("TOKEN").originalUrl("alreadyUsed").build());
            given_originalUrl("http://originalurl");

            when_createNewShortUrlEntityRetryable();

            then_exceptionThrown(ShortUrlTokenAlreadyUsedException.class, "[originalUrl=http://originalurl,shortUrlToken=TOKEN]");
        }

        private void given_originalUrl(final String originalUrl) {
            this.parameterOriginalUrl = originalUrl;
        }

        private void when_createNewShortUrlEntityRetryable() {
            try {
                resultShortUrlEntity = shortUrlDao.createNewShortUrlEntityRetryable(parameterOriginalUrl);
            } catch(Exception e) {
                caughtException = e;
            }
        }

        private void then_noException() {
            assertNoException(caughtException);
        }

        private void then_exceptionThrown(final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
            assertException(caughtException, expectedException, expectedExceptionMessage);
        }


        private void then_repositoryFindByTokenIsCalled(final String token) {
            then_noException();
            verify(mockUrlTokensRepository, times(1)).findByToken(token);
        }

        private void then_repositorySaveIsCalled(final ShortUrlEntity entity) {
            then_noException();
            verify(mockUrlTokensRepository, times(1)).save(entity);
        }

        private void then_resultShortUrlEntityIs(final ShortUrlEntity expectedShortUrl) {
            then_noException();
            assertEquals(expectedShortUrl, resultShortUrlEntity);
        }

    }
}
