package com.project.urlshortener.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Persistence entity for the SHORT_URL table in the database.<br/>
 * This table describes an association between a SHORT URL TOKEN and an ORIGINAL URL.
 */
@Entity(name = "SHORT_URL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrlEntity {

    /**
     * Internal identifier in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique token for a short url.
     */
    @Column(unique=true)
    private String token;

    /**
     * Original complete url matching a unique token.
     */
    @Column(unique=true)
    private String originalUrl;

}
