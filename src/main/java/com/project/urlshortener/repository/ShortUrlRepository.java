package com.project.urlshortener.repository;

import com.project.urlshortener.model.entities.ShortUrlEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Repository for ShortUrlEntity.
 */
@Repository
public interface ShortUrlRepository extends CrudRepository<ShortUrlEntity, Long> {

    /**
     * Search for a ShortUrlEntity for a token.
     * @param token value of the token
     * @return the ShortUrlEntity found in the repository. Returns null if not found.
     */
    ShortUrlEntity findByToken(final String token);

    /**
     * Search for a ShortUrlEntity for an original url.
     * @param originalUrl value of the original url
     * @return the ShortUrlEntity found in the repository. Returns null if not found.
     */
    ShortUrlEntity findByOriginalUrl(final String originalUrl);
}
