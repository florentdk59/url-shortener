package com.project.urlshortener.model.api.decodeshorturl;

import com.project.urlshortener.common.model.RestBasicResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The JSON response for when you wanted to read a short url and retrieves its original complete url.<br/>
 * The response contains the original complete url.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UrlShortenerDecodeShortUrlResponse extends RestBasicResponse {

    /**
     * The original complete url found for the matching token in the short url.<br/>
     * Can be null if the request failed.
     */
    private String originalCompleteUrl;

}
