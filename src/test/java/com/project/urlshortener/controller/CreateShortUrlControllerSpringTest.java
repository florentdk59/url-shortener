package com.project.urlshortener.controller;

import com.project.urlshortener.configuration.properties.UrlShortenerProperties;
import com.project.urlshortener.model.api.createshorturl.UrlShortenerCreateShortUrlResponse;
import com.project.urlshortener.model.entities.ShortUrlEntity;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.service.UrlShortenerService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static com.project.urlshortener.utils.AssertionUtils.assertStringStartsWith;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CreateShortUrlControllerSpringTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ShortUrlRepository shortUrlRepository;

	@Autowired
	private UrlShortenerProperties urlShortenerProperties;


	@Nested
	@DisplayName("POST / Spring tests")
	class CreateShortUrlTest {

		private String optionalParameterLanguageParam;
		private String jsonObject;
		private MediaType contentType;
		private ResponseEntity<UrlShortenerCreateShortUrlResponse> responseEntity;
		private ResponseEntity<UrlShortenerCreateShortUrlResponse> responseEntitySecondCall;

		@Autowired
		private UrlShortenerService spyUrlShortenerService;

		@BeforeEach
		void setUp() {
			this.optionalParameterLanguageParam = null;
			this.jsonObject = null;
			this.contentType = null;
			this.responseEntity = null;
			spyUrlShortenerService = Mockito.spy(spyUrlShortenerService);
		}

		@Test
		@DisplayName("POST / : when the original url already exists in the database, then httpStatus is OK, the short url is returned")
		void decodeShortUrl_TokenFound() {
			given_alreadyExistingUrl("https://www.journaldemontreal.com/", "abcdeFGHIJ");
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
				"url": "https://www.journaldemontreal.com/"
			}
			""");

			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.OK);
			then_responseSuccessIs(true);
			then_responseShortUrlIs("http://junit-hostname/abcdeFGHIJ");
			then_responseErrorIs(null);
		}

		@Test
		@DisplayName("POST / : when the original url does not already exist in the database, then httpStatus is OK, the new short url is returned")
		void decodeShortUrl_NewTokenCreated() {
			given_alreadyExistingUrl("https://www.journaldemontreal.com/", "abcdeFGHIJ");
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
				"url": "https://www.tvanouvelles.ca/"
			}
			""");

			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.OK);
			then_responseSuccessIs(true);
			then_responseShortUrlStartsWith("http://junit-hostname/");
			then_responseShortUrlIsNot("http://junit-hostname/abcdeFGHIJ");
			then_responseErrorIs(null);
		}

		@Test
		@DisplayName("POST / : when the original url does not already exist in the database and it is created twice, then httpStatus is OK, the both short url are the same")
		void decodeShortUrl_UrlCreatedTwiceSameShortUrl() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
				"url": "https://www.lapresse.ca/"
			}
			""");

			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.OK);
			then_responseSuccessIs(true);
			then_responseShortUrlStartsWith("http://junit-hostname/");
			then_responseShortUrlIsNot("http://junit-hostname/abcdeFGHIJ");
			then_responseErrorIs(null);

			when_callEndpointCreateShortUrl_secondCall();
			then_then_responseShortUrlIAndResponseShortUrlSecondCallAreTheSame();

		}

		@Test
		@DisplayName("POST / : when the original url is not a valid url, then httpStatus is BAD_REQUEST, and error is error.shorturl.InvalidUrl")
		void decodeShortUrl_error_invalidUrl() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
				"url": "https.ca/"
			}
			""");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The url [https.ca/] is invalid.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The url [https.ca/] is invalid.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("L'url [https.ca/] est invalide.");
		}

		@Test
		@DisplayName("POST / : when the original url is empty, then httpStatus is BAD_REQUEST, and error is error.required.NotBlank")
		void decodeShortUrl_error_emptyUrl() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
				"url": ""
			}
			""");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("La valeur pour le champ[url] ne peut pas \u00EAtre une cha\u00EEne vide.");
		}

		@Test
		@DisplayName("POST / : when the original url is not in the request, then httpStatus is BAD_REQUEST, and error is error.required.NotBlank")
		void decodeShortUrl_error_noUrl() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
			}
			""");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("La valeur pour le champ[url] ne peut pas \u00EAtre une cha\u00EEne vide.");
		}

		@Test
		@DisplayName("POST / : when the original url is null, then httpStatus is BAD_REQUEST, and error is error.required.NotBlank")
		void decodeShortUrl_error_nullUrl() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
				"url": null
			}
			""");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("La valeur pour le champ[url] ne peut pas \u00EAtre une cha\u00EEne vide.");
		}

		@Test
		@DisplayName("POST / : when the original url is blank, then httpStatus is BAD_REQUEST, and error is error.required.NotBlank")
		void decodeShortUrl_error_blankUrl() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""
			{
				"url": "    "
			}
			""");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The value for the field[url] cannot be blank.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("La valeur pour le champ[url] ne peut pas \u00EAtre une cha\u00EEne vide.");
		}

		@Test
		@DisplayName("POST / : when the json body is empty, then httpStatus is BAD_REQUEST, and error is error.json.body.invalid")
		void decodeShortUrl_error_emptyBody() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("""

			""");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The JSON was invalid for the body of this request.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The JSON was invalid for the body of this request.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("Le JSON est invalide pour le corps (body) de cette requ\u00EAte.");
		}

		@Test
		@DisplayName("POST / : when the json body is empty, then httpStatus is BAD_REQUEST, and error is error.json.body.invalid")
		void decodeShortUrl_error_invalidJson() {
			given_contentType(MediaType.APPLICATION_JSON);
			given_jsonRequest("prosperyouplaboum c'est le roi du pain d'épices");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The JSON was invalid for the body of this request.");

			when_callEndpointCreateShortUrl_secondCall();
			then_then_responseShortUrlIAndResponseShortUrlSecondCallAreTheSame();

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The JSON was invalid for the body of this request.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("Le JSON est invalide pour le corps (body) de cette requ\u00EAte.");
		}

		@Test
		@DisplayName("POST / : when the content type for the request is not application/json, then httpStatus is BAD_REQUEST, and error is error.rest.content.type")
		void decodeShortUrl_error_wrongContentType() {
			given_contentType(MediaType.TEXT_PLAIN);
			given_jsonRequest("""
			{
				"url": "https://www.lapresse.ca/"
			}
			""");

			// default locale
			when_callEndpointCreateShortUrl();

			then_responseHttpCodeIs(HttpStatus.BAD_REQUEST);
			then_responseSuccessIs(false);
			then_responseShortUrlIs(null);
			then_responseErrorIs("The content type is invalid.");

			// english locale
			given_languageParam("lang=en");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("The content type is invalid.");

			// french locale
			given_languageParam("lang=fr");
			when_callEndpointCreateShortUrl();
			then_responseErrorIs("Le type de contenu (content type) est invalide.");
		}

		private void given_contentType(final MediaType contentType) {
			this.contentType = contentType;
		}

		private void given_alreadyExistingUrl(final String originalUrl, final String shortUrlToken) {
			shortUrlRepository.save(ShortUrlEntity.builder().token(shortUrlToken).originalUrl(originalUrl).build());
		}

		private void given_jsonRequest(final String json) {
			jsonObject = json;

		}

		private void given_languageParam(final String langParam) {
			this.optionalParameterLanguageParam = langParam;
		}

		private void when_callEndpointCreateShortUrl() {
			HttpHeaders headers = new HttpHeaders();
			if (contentType != null) {
				headers.setContentType(contentType);
			}
			HttpEntity<String> httpRequest = new HttpEntity<>(jsonObject != null ? jsonObject : "", headers);

			String endpoint = "/" + (optionalParameterLanguageParam != null ? "?" + optionalParameterLanguageParam : StringUtils.EMPTY);
			responseEntity = restTemplate.exchange(endpoint, HttpMethod.POST, httpRequest, new ParameterizedTypeReference<>(){});
		}

		private void when_callEndpointCreateShortUrl_secondCall() {
			ResponseEntity<UrlShortenerCreateShortUrlResponse> backupFirstCall = responseEntity;
			when_callEndpointCreateShortUrl();
			responseEntitySecondCall = responseEntity;
			responseEntity = backupFirstCall;
		}

		private void then_responseHttpCodeIs(final HttpStatus expectedHttpStatus) {
			assertNotNull(responseEntity);
			assertEquals(expectedHttpStatus, responseEntity.getStatusCode());
		}

		private void then_responseShortUrlIs(final String shorturl) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			if (shorturl == null) {
				assertNull(responseEntity.getBody().getShortUrl());
			} else {
				assertEquals(shorturl, responseEntity.getBody().getShortUrl());
			}
		}

		private void then_responseShortUrlIsNot(final String shorturl) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			assertNotEquals(shorturl, responseEntity.getBody().getShortUrl());
		}

		private void then_then_responseShortUrlIAndResponseShortUrlSecondCallAreTheSame() {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			assertNotNull(responseEntitySecondCall);
			assertNotNull(responseEntitySecondCall.getBody());
			assertEquals(responseEntity.getBody().getShortUrl(), responseEntitySecondCall.getBody().getShortUrl());
		}

		private void then_responseShortUrlStartsWith(final String shorturl) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			assertStringStartsWith(shorturl, responseEntity.getBody().getShortUrl());
		}

		private void then_responseSuccessIs(final boolean expectedSuccess) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			assertEquals(expectedSuccess, responseEntity.getBody().isSuccess());
		}

		private void then_responseErrorIs(final String expectedError) {
			assertNotNull(responseEntity);
			assertNotNull(responseEntity.getBody());
			if (expectedError == null) {
				assertNull(responseEntity.getBody().getError());
			} else {
				assertEquals(expectedError, responseEntity.getBody().getError());
			}
		}

	}

}
