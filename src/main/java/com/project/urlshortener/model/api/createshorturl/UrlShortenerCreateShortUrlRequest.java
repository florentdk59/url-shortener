package com.project.urlshortener.model.api.createshorturl;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The JSON request when you want to create a short url for a normal url.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlShortenerCreateShortUrlRequest {

    /**
     * The value of the original normal url.
     */
    @NotBlank
    private String url;

}
