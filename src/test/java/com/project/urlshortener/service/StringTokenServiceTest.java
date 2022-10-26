package com.project.urlshortener.service;

import com.project.urlshortener.common.exception.RequiredValueException;
import com.project.urlshortener.service.impl.StringTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static com.project.urlshortener.utils.AssertionUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class StringTokenServiceTest {

    @Nested
    @DisplayName("StringTokenService.createStringToken tests")
    class CreateStringTokenTest {
        private static final String ALL_AVAILABLE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        private String parametersAvailableCharacters;
        private Integer parameterNbCharacters;
        private String resultStringToken;
        private Exception caughtException;
        private StringTokenService service;

        @BeforeEach
        void setUp() throws NoSuchAlgorithmException {
            parametersAvailableCharacters = null;
            parameterNbCharacters = null;
            resultStringToken = null;
            caughtException = null;
            service = new StringTokenServiceImpl();
        }

        @Test
        @DisplayName("createStringToken : result string should have 10 characters, and all characters should be from ALL_AVAILABLE_CHARACTERS")
        void createStringToken_tenCharacters() {
            given_availableCharacters(ALL_AVAILABLE_CHARACTERS);
            given_numberOfCharacters(10);

            when_createStringToken();

            then_resultStringTokenHasExpectedLengthAndCharacters(10, ALL_AVAILABLE_CHARACTERS);
        }

        @Test
        @DisplayName("createStringToken : result string should have 5 characters, and all characters should be from ALL_AVAILABLE_CHARACTERS")
        void createStringToken_fiveCharacters() {
            given_availableCharacters(ALL_AVAILABLE_CHARACTERS);
            given_numberOfCharacters(5);

            when_createStringToken();

            then_resultStringTokenHasExpectedLengthAndCharacters(5, ALL_AVAILABLE_CHARACTERS);
        }

        @Test
        @DisplayName("createStringToken : result string should have 10 characters, and all characters should be from ALL_AVAILABLE_CHARACTERS")
        void createStringToken_tenCharacters_onlyOneAvailableCharacter() {
            given_availableCharacters("Z");
            given_numberOfCharacters(10);

            when_createStringToken();

            then_resultStringTokenEquals("ZZZZZZZZZZ");
        }


        @Test
        @DisplayName("createStringToken : if available characters are null, this triggers an error")
        void createStringToken_error_noAvailableCharacters_null() {
            given_availableCharacters(null);
            given_numberOfCharacters(10);

            when_createStringToken();

            then_exceptionThrown(RequiredValueException.class, "fieldName=availableCharacters,requirementType=CANNOT_BE_NULL");
        }

        @Test
        @DisplayName("createStringToken : if available characters are empty, this triggers an error")
        void createStringToken_error_noAvailableCharacters_empty() {
            given_availableCharacters("");
            given_numberOfCharacters(10);

            when_createStringToken();

            then_exceptionThrown(RequiredValueException.class, "fieldName=availableCharacters,requirementType=CANNOT_BE_EMPTY");
        }

        @Test
        @DisplayName("createStringToken : if available characters are blank, this triggers an error")
        void createStringToken_error_noAvailableCharacters_blank() {
            given_availableCharacters("           ");
            given_numberOfCharacters(10);

            when_createStringToken();

            then_exceptionThrown(RequiredValueException.class, "fieldName=availableCharacters,requirementType=CANNOT_BE_BLANK");
        }

        @Test
        @DisplayName("createStringToken : if you ask for a token of zero character, this triggers an error")
        void createStringToken_error_numberOfCharactersIsZero() {
            given_availableCharacters(ALL_AVAILABLE_CHARACTERS);
            given_numberOfCharacters(0);

            when_createStringToken();

            then_exceptionThrown(RequiredValueException.class, "fieldName=nbCharacters,requirementType=CANNOT_BE_ZERO");
        }

        @Test
        @DisplayName("createStringToken : if you ask a token of a negative amount of characters, this triggers an error")
        void createStringToken_error_numberOfCharactersIsNegative() {
            given_availableCharacters(ALL_AVAILABLE_CHARACTERS);
            given_numberOfCharacters(-5);

            when_createStringToken();

            then_exceptionThrown(RequiredValueException.class, "fieldName=nbCharacters,requirementType=CANNOT_BE_NEGATIVE");
        }


        private void given_numberOfCharacters(final int nbCharacters) {
            this.parameterNbCharacters = nbCharacters;
        }

        private void given_availableCharacters(final String availableCharacters) {
            this.parametersAvailableCharacters = availableCharacters;
        }

        private void when_createStringToken() {
            try {
                resultStringToken = service.createStringToken(parametersAvailableCharacters, parameterNbCharacters);
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

        private void then_resultStringTokenHasExpectedLengthAndCharacters(final int expectedTokenLength, final String expectedCharacters) {
            then_noException();
            assertNotNull(resultStringToken);

            // length of the token
            assertEquals(expectedTokenLength, resultStringToken.length());

            // content of the token
            resultStringToken.chars().forEach(intChar -> {
                if (expectedCharacters.indexOf(intChar) < 0) {
                    fail(String.format("FAIL : characters in resultStringToken[%s] are not all present in expectedCharacters[%s]", resultStringToken, expectedCharacters));
                }
            });
        }

        private void then_resultStringTokenEquals(final String expectedToken) {
            then_noException();
            assertEquals(expectedToken, resultStringToken);
        }


    }
}
