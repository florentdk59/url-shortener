package com.project.urlshortener.controller;

import com.project.urlshortener.common.controller.AbstractCommonController;
import com.project.urlshortener.common.model.RestBasicResponse;
import com.project.urlshortener.exception.ShortUrlInvalidTokenException;
import com.project.urlshortener.exception.ShortUrlTokenNotFoundException;
import com.project.urlshortener.model.api.decodeshorturl.UrlShortenerDecodeShortUrlResponse;
import com.project.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller : GET / endpoint (decode a short url).
 */
@RestController
@RequestMapping("/")
@Validated
@RequiredArgsConstructor
@Slf4j
public class DecodeShortUrlController extends AbstractCommonController {


	/** Services for reading and creating short url tokens. */
	private final UrlShortenerService urlShortenerService;

	/** Access to the localized messages of the application. */
	private final MessageSource messageSource;

	/**
	 * Reads a short url token and tries to find the matching original url.<br/>
	 *
	 * @param shortUrlToken the short url token (normally a 10 character string token).
	 * @return ResponseEntity with a UrlShortenerDecodeShortUrlResponse with the originalCompleteUrl.
	 * @throws ShortUrlTokenNotFoundException If the token does not exist, a ShortUrlTokenNotFoundException will be thrown.
	 * @throws ShortUrlInvalidTokenException If the token is empty or invalid, a ShortUrlInvalidTokenException will be thrown.
	 */
	@GetMapping("/{short-url-token}")
	public ResponseEntity<UrlShortenerDecodeShortUrlResponse> decodeShortUrl(final @PathVariable("short-url-token") String shortUrlToken) throws ShortUrlTokenNotFoundException, ShortUrlInvalidTokenException {

		String originalUrl = urlShortenerService.getOriginalUrlForShortUrlToken(shortUrlToken);
		return ResponseEntity.ok(
				UrlShortenerDecodeShortUrlResponse.builder()
						.originalCompleteUrl(originalUrl)
						.success(true)
						.build()
		);

	}

	/*
	 * Alternate endpoint for decodeShortUrl, for when the user does not provide a short-url-token at all.<br/>
	 * This endpoint always fails because it just calls decodeShortUrl with an empty string, resulting in a ShortUrlInvalidTokenException.<br/>
	 * This endpoint exists because otherwise, the controller would say that the Method Type is invalid (referencing the POST endpoint in CreateShortUrlController which share the same path on /).
	 */
	@GetMapping("/")
	public ResponseEntity<UrlShortenerDecodeShortUrlResponse> decodeShortUrl() throws ShortUrlInvalidTokenException, ShortUrlTokenNotFoundException {
		return decodeShortUrl(StringUtils.EMPTY);
	}

	/**
	 * Exception Handler for ShortUrlTokenNotFoundException.<br/>
	 * Triggers a NOT_FOUND response code.
	 *
	 * @param sutnfe ShortUrlTokenNotFoundException an exception occurring when a short url token does not have any known match for an original url.
	 * @return RestBasicResponse with a localized error message.
	 */
	@ExceptionHandler(ShortUrlTokenNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public RestBasicResponse onShortUrlTokenNotFoundException(final ShortUrlTokenNotFoundException sutnfe) {
		return handleExceptionWithLocalizedMessage(sutnfe);
	}

	/**
	 * Exception Handler for ShortUrlInvalidTokenException.<br/>
	 * Triggers a BAD_REQUEST response code.
	 *
	 * @param suite ShortUrlInvalidTokenException an exception occurring when a short url token is null or empty.
	 * @return RestBasicResponse with a localized error message.
	 */
	@ExceptionHandler(ShortUrlInvalidTokenException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestBasicResponse onShortUrlInvalidTokenException(final ShortUrlInvalidTokenException suite) {
		return handleExceptionWithLocalizedMessage(suite);
	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected MessageSource getMessageSource() {
		return this.messageSource;
	}
}
