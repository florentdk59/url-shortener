package com.project.urlshortener.common.controller;

import com.project.urlshortener.common.exception.ExceptionWithMessageKey;
import com.project.urlshortener.common.exception.InvalidJsonBodyException;
import com.project.urlshortener.common.exception.InvalidRequestContentTypeException;
import com.project.urlshortener.common.exception.RequiredValueException;
import com.project.urlshortener.common.model.RestBasicResponse;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

/**
 * AbstractCommonController.<br/>
 * The base for a Spring Rest Controller.<br/>
 * This class offers ExceptionHandlers for a couple of major exceptions.
 */
public abstract class AbstractCommonController {

	/**
	 * Access to the logger for the Controller extending this abstract class.
	 *
	 * @return Logger a lo4j logger.
	 */
	protected abstract Logger getLogger();

	/**
	 * Access to the spring message source for the Controller extending this abstract class.
	 *
	 * @return MessageSource the localized messages for the application.
	 */
	protected abstract MessageSource getMessageSource();

	/**
	 * Exception Handler for MethodArgumentNotValidException.<br/>
	 * Triggers a BAD_REQUEST response code.
	 *
	 * @param manve MethodArgumentNotValidException an exception that occurs when a REST endpoint fails because of Validation issues.
	 * @return RestBasicResponse with a localized error message.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestBasicResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException manve) {
		return handleExceptionWithLocalizedMessage(new RequiredValueException(manve));
	}

	/**
	 * Exception Handler for HttpMessageNotReadableException.<br/>
	 * Triggers a BAD_REQUEST response code.
	 *
	 * @param hmnre HttpMessageNotReadableException an exception that occurs when the json message for the REST is broken.
	 * @return RestBasicResponse with a localized error message.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestBasicResponse onHttpMessageNotReadableException(final HttpMessageNotReadableException hmnre) {
		return handleExceptionWithLocalizedMessage(new InvalidJsonBodyException(hmnre));
	}

	/**
	 * Exception Handler for RequiredValueException.<br/>
	 * Triggers an INTERNAL_SERVER_ERROR response code.
	 *
	 * @param rve RequiredValueException an exception that occurs when ArgumentUtils fails because of an unexcepted bad value in the code.
	 * @return RestBasicResponse with a localized error message based on the RequiredValueException type.
	 */
	@ExceptionHandler(RequiredValueException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public RestBasicResponse onRequiredValueException(final RequiredValueException rve) {
		if (getLogger() != null && getLogger().isErrorEnabled()) {
			getLogger().error(String.format("An unexpected RequiredValueException has occurred : %s", rve), rve);
		}
		// get error message from RequiredValueException and from the localized messages
		return handleExceptionWithLocalizedMessage(rve);
	}

	/**
	 * Exception Handler for HttpMediaTypeNotSupportedException.<br/>
	 * Triggers an INTERNAL_SERVER_ERROR response code.
	 *
	 * @param hmtnse HttpMediaTypeNotSupportedException an exception that occurs when a request is made with the wrong content type and the content type was rejected.
	 * @return RestBasicResponse with a default error message and some minor details on the error.
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestBasicResponse onHttpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException hmtnse) {
		return handleExceptionWithLocalizedMessage(new InvalidRequestContentTypeException(hmtnse));
	}

	/**
	 * Exception Handler for any non-specific Throwable.<br/>
	 * Triggers an INTERNAL_SERVER_ERROR response code.
	 *
	 * @param t Throwable an error that was not caught by any other Handler passed the controller.
	 * @return RestBasicResponse with a default error message and some minor details on the error.
	 */
	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public RestBasicResponse onUnexpectedError(final Throwable t) {
		// default behaviour for an unexpected error
		if (getLogger() != null && getLogger().isErrorEnabled()) {
			getLogger().error(String.format("An error has occurred : %s", t), t);
		}
		return RestBasicResponse.builder().success(false).error(String.format("An unexpected error has occurred : %s", t)).build();
	}

	/**
	 * Uses the message source and the active local to build a localized error message from the ExceptionWithMessageKey.
	 *
	 * @param exception ExceptionWithMessageKey a class that has a message key and a string parameter.
	 * @return RestBasicResponse with a localized error message based on the ExceptionWithMessageKey.
	 */
	protected RestBasicResponse handleExceptionWithLocalizedMessage(ExceptionWithMessageKey exception) {
		return RestBasicResponse.builder().success(false).error(getLocalizedErrorMessage(exception, getMessageSource())).build();
	}

	/**
	 * Reads the messages sources and the ExceptionWithMessageKey and returns a message string.
	 *
	 * @param error ExceptionWithMessageKey a class that has a message key and a string parameter.
	 * @param messageSource MessageSource the localized messages for the application.
	 * @return String the message matching the key in the ExceptionWithMessageKey, according to the active Locale (LocaleContextHolder::getLocale).
	 * */
	protected String getLocalizedErrorMessage(final ExceptionWithMessageKey error, final MessageSource messageSource) {
		Objects.requireNonNull(error, "error cannot be null");
		Objects.requireNonNull(messageSource, "messageSource cannot be null");
		Objects.requireNonNull(error.getMessageKey(), "error.getMessageKey() cannot be null");

		try {
			return messageSource.getMessage(error.getMessageKey(), error.getMessageArguments(), LocaleContextHolder.getLocale());
		} catch(Exception e) {
			if (getLogger() != null && getLogger().isErrorEnabled()) {
				getLogger().error(String.format("Could not find message string for [%s] : %s", error.getMessageKey(), e));
			}
			return error.toString();
		}
	}

}
