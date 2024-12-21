package com.project.urlshortener.controller;

import com.project.urlshortener.common.controller.AbstractCommonController;
import com.project.urlshortener.common.model.RestBasicResponse;
import com.project.urlshortener.exception.*;
import com.project.urlshortener.model.api.createshorturl.UrlShortenerCreateShortUrlRequest;
import com.project.urlshortener.model.api.createshorturl.UrlShortenerCreateShortUrlResponse;
import com.project.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller : POST / endpoint (creates a short url from an original url).
 */
@RestController
@RequestMapping("/")
@Validated
@Slf4j
public class CreateShortUrlController extends AbstractCommonController {

	/** Services for reading and creating short url tokens. */
	private final UrlShortenerService urlShortenerService;

	/** Access to the localized messages of the application. */
	private final MessageSource messageSource;

	/**
	 * Default constructor for the UrlShortenerController.
	 *
	 * @param urlShortenerService instance of UrlShortenerService.
	 * @param messageSource instance of MessageSource.
	 */
	@Autowired
	public CreateShortUrlController(final UrlShortenerService urlShortenerService, final MessageSource messageSource) {
		this.urlShortenerService = urlShortenerService;
		this.messageSource = messageSource;
	}

	/**
	 * Creates a short url for an original url.<br/>
	 *
	 * @param request UrlShortenerCreateShortUrlRequest with the url
	 * @return ResponseEntity with a UrlShortenerCreateShortUrlResponse with the shortUrl (base url + token).
	 * @throws ShortUrlTokenCannotBeCreatedException If the no short url token could be created for technical issues.
	 * @throws ShortUrlTokenAlreadyUsedException If the no short url token could be created because no unique token could be created for technical issues despite retries.
	 * @throws ShortUrlInvalidUrlException If the provided url is not a valid url.
	 */
	@PostMapping("/")
	public ResponseEntity<UrlShortenerCreateShortUrlResponse> createShortUrl(final @Valid @RequestBody UrlShortenerCreateShortUrlRequest request) throws ShortUrlTokenCannotBeCreatedException, ShortUrlInvalidUrlException, ShortUrlTokenAlreadyUsedException {

		String shortUrl = urlShortenerService.obtainShortUrlForOriginalCompleteUrl(request.getUrl());
		return ResponseEntity.ok(
				UrlShortenerCreateShortUrlResponse.builder()
					.shortUrl(shortUrl)
					.success(true)
					.build()
		);

	}

	/**
	 * Exception Handler for ShortUrlInvalidUrlException.<br/>
	 * Triggers a BAD_REQUEST response code.
	 *
	 * @param suiue ShortUrlInvalidUrlException an exception occurring when an original url is not a valid url.
	 * @return RestBasicResponse with a localized error message.
	 */
	@ExceptionHandler(ShortUrlInvalidUrlException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestBasicResponse onShortUrlInvalidUrlException(final ShortUrlInvalidUrlException suiue) {
		return handleExceptionWithLocalizedMessage(suiue);
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
