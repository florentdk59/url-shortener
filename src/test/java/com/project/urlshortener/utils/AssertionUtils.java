package com.project.urlshortener.utils;

import io.micrometer.common.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AssertionUtils {

    public static void assertStringContains(final String expectedContainedString, final String testedString) {
        if (StringUtils.isEmpty(testedString)) {
            fail(String.format("FAIL : testedString[%s] cannot be null or empty", testedString));
        }
        if (expectedContainedString == null) {
            fail("FAIL : expectedContainedString[null] cannot be null");
        }
        if (StringUtils.isEmpty(expectedContainedString) || !testedString.contains(expectedContainedString)) {
            fail(String.format("FAIL : testedString[%s] does not contain expectedContainedString[%s]", testedString, expectedContainedString));

        }

    }

    public static void assertStringStartsWith(final String expectedStringStart, final String testedString) {
        if (StringUtils.isEmpty(testedString)) {
            fail(String.format("FAIL : testedString[%s] cannot be null or empty", testedString));
        }
        if (expectedStringStart == null) {
            fail("FAIL : expectedStringStart[null] cannot be null");
        }
        if (!testedString.startsWith(expectedStringStart)) {
            fail(String.format("FAIL : testedString[%s] does not start with expectedStringStart[%s]", testedString, expectedStringStart));

        }

    }

    public static void assertNoException(final Exception e) {
        if (e != null) {
            e.printStackTrace();
            fail(String.format("FAIL : an exception was caught [%s]", e));
        }
    }

    public static void assertException(final Exception e, final Class<? extends Exception> expectedException, final String expectedExceptionMessage) {
        if (e == null) {
            fail(String.format("FAIL : expectedException[%s] but caughtException is null", expectedException));
        }
        assertEquals(expectedException, e.getClass());
        assertStringContains(expectedExceptionMessage, e.toString());
    }
}
