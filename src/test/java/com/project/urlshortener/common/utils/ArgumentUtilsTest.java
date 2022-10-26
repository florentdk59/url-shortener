package com.project.urlshortener.common.utils;

import com.project.urlshortener.common.exception.RequiredValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.project.urlshortener.utils.AssertionUtils.assertException;
import static com.project.urlshortener.utils.AssertionUtils.assertNoException;

public class ArgumentUtilsTest {

    @Nested
    @DisplayName("ArgumentUtils.requireNonNull tests")
    class RequireNonNullTest {

        private Object parameterObject;
        private String parameterFieldName;
        private Exception caughtException;

        @BeforeEach
        void setUp() {
            this.parameterObject = null;
            this.parameterFieldName = null;
            this.caughtException = null;
        }

        @Test
        @DisplayName("requireNonNull : when object is not null, then no exception")
        void requireNonNull_objectIsNotNull() {
            given_object("value");
            given_fieldName("fieldname");

            when_requireNonNull();

            then_noException();
        }

        @Test
        @DisplayName("requireNonNull : when object is null, then RequiredValueException CANNOT_BE_NULL and fieldname is used")
        void requireNonNull_objectIsNull() {
            given_object(null);
            given_fieldName("fieldname");

            when_requireNonNull();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_NULL]");
        }

        @Test
        @DisplayName("requireNonNull : when object is empty, then no exception")
        void requireNonNull_objectIsEmpty() {
            given_object("");
            given_fieldName("fieldname");

            when_requireNonNull();

            then_noException();
        }

        @Test
        @DisplayName("requireNonNull : when object is blank, then no exception")
        void requireNonNull_objectIsBlank() {
            given_object("  ");
            given_fieldName("fieldname");

            when_requireNonNull();

            then_noException();
        }

        private void given_object(final Object o) {
            this.parameterObject = o;
        }

        private void given_fieldName(final String fieldName) {
            this.parameterFieldName = fieldName;
        }

        private void when_requireNonNull() {
            try {
                ArgumentUtils.requireNonNull(parameterObject, parameterFieldName);
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

    }

    @Nested
    @DisplayName("ArgumentUtils.requireNonEmpty tests")
    class RequireNonEmptyTest {

        private String parameterString;
        private String parameterFieldName;
        private Exception caughtException;

        @BeforeEach
        void setUp() {
            this.parameterString = null;
            this.parameterFieldName = null;
            this.caughtException = null;
        }

        @Test
        @DisplayName("requireNonEmpty : when string is not empty, then no exception")
        void requireNonEmpty_stringIsNotEmpty() {
            given_string("value");
            given_fieldName("fieldname");

            when_requireNonEmpty();

            then_noException();
        }

        @Test
        @DisplayName("requireNonEmpty : when object is null, then RequiredValueException CANNOT_BE_NULL and fieldname is used")
        void requireNonEmpty_objectIsNull() {
            given_string(null);
            given_fieldName("fieldname");

            when_requireNonEmpty();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_NULL]");
        }

        @Test
        @DisplayName("requireNonEmpty : when object is empty, then RequiredValueException CANNOT_BE_EMPTY and fieldname is used")
        void requireNonEmpty_objectIsEmpty() {
            given_string("");
            given_fieldName("fieldname");

            when_requireNonEmpty();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_EMPTY]");
        }

        @Test
        @DisplayName("requireNonEmpty : when object is blank, then no exception")
        void requireNonEmpty_objectIsBlank() {
            given_string("  ");
            given_fieldName("fieldname");

            when_requireNonEmpty();

            then_noException();
        }

        private void given_string(final String s) {
            this.parameterString = s;
        }

        private void given_fieldName(final String fieldName) {
            this.parameterFieldName = fieldName;
        }

        private void when_requireNonEmpty() {
            try {
                ArgumentUtils.requireNonEmpty(parameterString, parameterFieldName);
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

    }

    @Nested
    @DisplayName("ArgumentUtils.requireNonBlank tests")
    class RequireNonBlankTest {

        private String parameterString;
        private String parameterFieldName;
        private Exception caughtException;

        @BeforeEach
        void setUp() {
            this.parameterString = null;
            this.parameterFieldName = null;
            this.caughtException = null;
        }

        @Test
        @DisplayName("requireNonBlank : when string is not blank, then no exception")
        void requireNonBlank_stringIsNotEmpty() {
            given_string("value");
            given_fieldName("fieldname");

            when_requireNonBlank();

            then_noException();
        }

        @Test
        @DisplayName("requireNonBlank : when object is null, then RequiredValueException CANNOT_BE_NULL and fieldname is used")
        void requireNonBlank_objectIsNull() {
            given_string(null);
            given_fieldName("fieldname");

            when_requireNonBlank();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_NULL]");
        }

        @Test
        @DisplayName("requireNonBlank : when object is empty, then RequiredValueException CANNOT_BE_EMPTY and fieldname is used")
        void requireNonBlank_objectIsEmpty() {
            given_string("");
            given_fieldName("fieldname");

            when_requireNonBlank();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_EMPTY]");
        }

        @Test
        @DisplayName("requireNonBlank : when object is blank, then RequiredValueException CANNOT_BE_BLANK and fieldname is used")
        void requireNonBlank_objectIsBlank() {
            given_string("  ");
            given_fieldName("fieldname");

            when_requireNonBlank();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_BLANK]");
        }

        private void given_string(final String s) {
            this.parameterString = s;
        }

        private void given_fieldName(final String fieldName) {
            this.parameterFieldName = fieldName;
        }

        private void when_requireNonBlank() {
            try {
                ArgumentUtils.requireNonBlank(parameterString, parameterFieldName);
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

    }

    @Nested
    @DisplayName("ArgumentUtils.requireStrictlyPositiveValue tests")
    class RequireStrictlyPositiveValueTest {

        private Integer parameterInteger;
        private String parameterFieldName;
        private Exception caughtException;

        @BeforeEach
        void setUp() {
            this.parameterInteger = null;
            this.parameterFieldName = null;
            this.caughtException = null;
        }

        @Test
        @DisplayName("requireStrictlyPositiveValue : when integer is a positive value, then no exception")
        void requireStrictlyPositiveValue_integerIsPositiveNumber() {
            given_integer(50);
            given_fieldName("fieldname");

            when_requireStrictlyPositiveValue();

            then_noException();
        }

        @Test
        @DisplayName("requireStrictlyPositiveValue : when integer is zero, then RequiredValueException CANNOT_BE_ZERO with field name")
        void requireStrictlyPositiveValue_error_integerIsZero() {
            given_integer(0);
            given_fieldName("fieldname");

            when_requireStrictlyPositiveValue();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_ZERO]");
        }

        @Test
        @DisplayName("requireStrictlyPositiveValue : when integer is a negative value, then RequiredValueException CANNOT_BE_NEGATIVE with field name")
        void requireStrictlyPositiveValue_error_integerIsNegative() {
            given_integer(-12);
            given_fieldName("fieldname");

            when_requireStrictlyPositiveValue();

            then_exceptionThrown(RequiredValueException.class, "[fieldName=fieldname,requirementType=CANNOT_BE_NEGATIVE]");
        }

        private void given_integer(final Integer i) {
            this.parameterInteger = i;
        }

        private void given_fieldName(final String fieldName) {
            this.parameterFieldName = fieldName;
        }

        private void when_requireStrictlyPositiveValue() {
            try {
                ArgumentUtils.requireStrictlyPositiveValue(parameterInteger, parameterFieldName);
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

    }

}
