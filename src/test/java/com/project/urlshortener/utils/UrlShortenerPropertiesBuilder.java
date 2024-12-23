package com.project.urlshortener.utils;

import com.project.urlshortener.configuration.properties.UrlShortenerProperties;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.spy;

public class UrlShortenerPropertiesBuilder {

    public static final String TOKEN_CHARACTERS = "tokenCharacters";
    public static final String TOKEN_MAX_ATTEMPTS = "tokenMaxAttempts";
    public static final String TOKEN_LENGTH = "tokenLength";
    public static final String BASE_URL = "baseUrl";
    private final Map<String, String> values;

    public UrlShortenerPropertiesBuilder() {
        values = new HashMap<>();
        withBaseUrl("http://junit-fake-url/");
        withTokenLength(10);
        withTokenMaxAttempts(5);
        withTokenCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    public UrlShortenerPropertiesBuilder withBaseUrl(final String baseUrl) {
        values.put(BASE_URL, baseUrl);
        return this;
    }

    public UrlShortenerPropertiesBuilder withTokenLength(final int length) {
        values.put(TOKEN_LENGTH, String.valueOf(length));
        return this;
    }

    public UrlShortenerPropertiesBuilder withTokenMaxAttempts(final int maxAttempts) {
        values.put(TOKEN_MAX_ATTEMPTS, String.valueOf(maxAttempts));
        return this;
    }

    public UrlShortenerPropertiesBuilder withTokenCharacters(final String characters) {
        values.put(TOKEN_CHARACTERS, characters);
        return this;
    }

    private String readStringValue(String key) {
        return values.get(key);
    }

    private Integer readIntegerValue(String key) {
        return Integer.parseInt(readStringValue(key));
    }

    public UrlShortenerProperties buildSpy() {
        var baseUrl = readStringValue(BASE_URL);

        int tokenLength = readIntegerValue(TOKEN_LENGTH);
        int tokenMaxAttempts = readIntegerValue(TOKEN_MAX_ATTEMPTS);
        String tokenCharacters = readStringValue(TOKEN_CHARACTERS);

        var token = spy(new UrlShortenerProperties.Token(tokenLength, tokenMaxAttempts, tokenCharacters));
        return spy(new UrlShortenerProperties(baseUrl, token));
    }

}
