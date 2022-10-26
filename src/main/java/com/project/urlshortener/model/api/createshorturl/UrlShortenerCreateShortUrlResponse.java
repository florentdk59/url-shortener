package com.project.urlshortener.model.api.createshorturl;

import com.project.urlshortener.common.model.RestBasicResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The JSON response for when you wanted to create a short url for a complete url.<br/>
 * The response contains the short url.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UrlShortenerCreateShortUrlResponse extends RestBasicResponse {

    /**
     * The short url found or created for the original complete url.<br/>
     * Can be null if the request failed.
     */
    private String shortUrl;

}
