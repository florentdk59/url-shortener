package com.project.urlshortener.common.exception;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An Exception for when ArgumentUtils fails a validation.<br/>
 * This exception contains a basic description of the field name (fieldName) that fails, and what requirement made it fail (RequirementType).
 * This exception is a RuntimeException.
 */
@Getter
public class RequiredValueException extends RuntimeException implements ExceptionWithMessageKey {

    private static final String MESSAGE_KEY = "error.required.%s";

    /** The name of the field that failed in ArgumentUtils. */
    private final String fieldName;

    /** The requirement that made the field fail in ArgumentUtils. */
    private final RequirementType requirementType;

    /**
     * Default constructor.
     *
     * @param fieldName the name of the field that failed.
     * @param requirementType the requirement that made the field fail in ArgumentUtils.
     */
    public RequiredValueException(final String fieldName, final RequirementType requirementType) {
        super(String.format("RequiredValueException : fieldName[%s] type[%s]", fieldName, requirementType));
        this.fieldName = fieldName;
        this.requirementType = requirementType;
    }

    /**
     * Alternate constructor, to accodomate a MethodArgumentNotValidException (rest endpoint parameter validation).
     *
     * @param manve MethodArgumentNotValidException when a rest endpoint parameter fails a validation
     */
    public RequiredValueException(final MethodArgumentNotValidException manve) {
        super(String.format("RequiredValueException : manve[%s]", manve));
        this.requirementType = RequirementType.fromMethodArgumentNotValidException(manve);
        this.fieldName = getFieldNameFromMethodArgumentNotValidException(manve);
    }

    /**
     * An enum for the requirement involved in argument utils.<br/>
     * This is the reason why the RequiredValueException was thrown.
     */
    @Getter
    public enum RequirementType {
        INVALID_FIELD("InvalidField"),
        CANNOT_BE_NULL("NotNull"),
        CANNOT_BE_EMPTY("NotEmpty"),
        CANNOT_BE_BLANK("NotBlank"),
        CANNOT_BE_NEGATIVE("NotNegative"),
        CANNOT_BE_ZERO("NotZero");

        private final String errorKey;

        RequirementType(String errorKey) {
            this.errorKey = errorKey;
        }

        /**
         * Obtain a RequirementType from a MethodArgumentNotValidException.<br/>
         * This method looks for the reason why the MethodArgumentNotValidException was throws, and tries to find a matching RequirementType.
         *
         * @param manve MethodArgumentNotValidException when a rest endpoint parameter fails a validation
         * @return a RequirementType matching the reason why the MethodArgumentNotValidException was thrown. Returns INVALID_FIELD by default.
         */
        public static RequirementType fromMethodArgumentNotValidException(final MethodArgumentNotValidException manve) {
            Objects.requireNonNull(manve);
            return Optional.ofNullable(fromObjectError(getObjectErrorFromMethodArgumentNotValidException(manve))).orElse(INVALID_FIELD);
        }

        private static RequirementType fromObjectError(final ObjectError objectError) {
            return Stream.of(RequirementType.values()).filter(type -> objectError != null && type.getErrorKey().equals(objectError.getCode())).findFirst().orElse(null);
        }

    }

    @Override
    public String getMessageKey() {
        Objects.requireNonNull(this.requirementType);
        return String.format(MESSAGE_KEY, this.requirementType.getErrorKey());
    }

    @Override
    public String[] getMessageArguments() {
        return new String[] { this.fieldName };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fieldName", fieldName)
                .append("requirementType", requirementType)
                .toString();
    }

    private static String getFieldNameFromMethodArgumentNotValidException(final MethodArgumentNotValidException manve) {
        Objects.requireNonNull(manve);
        ObjectError objectError = getObjectErrorFromMethodArgumentNotValidException(manve);
        if (objectError instanceof FieldError fieldError) {
            return fieldError.getField();
        }
        return null;
    }

    private static ObjectError getObjectErrorFromMethodArgumentNotValidException(final MethodArgumentNotValidException manve) {
        if (manve.getBindingResult() != null && !CollectionUtils.isEmpty(manve.getBindingResult().getAllErrors())) {
            return manve.getBindingResult().getAllErrors().getFirst();
        }
        return null;
    }
}
