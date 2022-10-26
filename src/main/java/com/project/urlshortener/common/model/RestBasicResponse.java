package com.project.urlshortener.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A basic JSON response for a REST endpoint.<br/>
 * Contains a boolean value 'success' and a string 'error'.<br/>
 * If the error is null, it won't appear in the resulting Json string.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RestBasicResponse {

    /** Indicates whether the rest endpoint ended with a success (true) or with a failure (false). */
    private boolean success;

    /** An error message describing a success=false situation.<br/>
     * If the error is "null", it won't appear in the resulting Json string. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

}
