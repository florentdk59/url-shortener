package com.project.urlshortener.common.utils;

import com.project.urlshortener.common.exception.RequiredValueException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * A basic utility class, offering standard validation tools.<br/>
 * These validations can all trigger an RequiredValueException if the tested condition is not met.<br/>
 * These validations are meant to be used to handle unexpected bad values in the code only.
 */
@UtilityClass
public class ArgumentUtils {

    /**
     * Requires an object to be not null.<br/>
     * Will throw a RequiredValueException if the object is null.
     *
     * @param o the object to be tested.
     * @param fieldName the name of the variable that was tested (will be used in an error message).
     */
    public static void requireNonNull(final Object o, final String fieldName) {
        if (o == null) {
            throw new RequiredValueException(fieldName, RequiredValueException.RequirementType.CANNOT_BE_NULL);
        }
    }

    /**
     * Requires a string to be not empty.<br/>
     * Will throw a RequiredValueException if the string is empty (null or no characters).
     *
     * @param s the string to be tested.
     * @param fieldName the name of the variable that was tested (will be used in an error message).
     */
    public static void requireNonEmpty(final String s, final String fieldName) {
        requireNonNull(s, fieldName);
        if (StringUtils.isEmpty(s)) {
            throw new RequiredValueException(fieldName, RequiredValueException.RequirementType.CANNOT_BE_EMPTY);
        }
    }

    /**
     * Requires a string to be not blank.<br/>
     * Will throw a RequiredValueException if the string is blank (null or no characters or all whitespace characters).
     *
     * @param s the string to be tested.
     * @param fieldName the name of the variable that was tested (will be used in an error message).
     */
    public static void requireNonBlank(final String s, final String fieldName) {
        requireNonEmpty(s, fieldName);
        if (StringUtils.isBlank(s)) {
            throw new RequiredValueException(fieldName, RequiredValueException.RequirementType.CANNOT_BE_BLANK);
        }
    }

    /**
     * Requires an Integer to be strictly greater than zero.<br/>
     * Will throw a RequiredValueException if the integer is zero or negative.
     *
     * @param i the Integer to be tested.
     * @param fieldName the name of the variable that was tested (will be used in an error message).
     */
    public static void requireStrictlyPositiveValue(final Integer i, final String fieldName) {
        requireNonNull(i, fieldName);
        if (i == 0) {
            throw new RequiredValueException(fieldName, RequiredValueException.RequirementType.CANNOT_BE_ZERO);
        }
        if (i < 0) {
            throw new RequiredValueException(fieldName, RequiredValueException.RequirementType.CANNOT_BE_NEGATIVE);
        }
    }
}
