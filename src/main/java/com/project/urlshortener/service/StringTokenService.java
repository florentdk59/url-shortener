package com.project.urlshortener.service;

/**
 * Service to create tokens of random characters.
 */
public interface StringTokenService {

    /**
     * Creates a string token of random characters.
     *
     * @param availableCharacters the characters contained in this string will be used as choices of the random characters..
     * @param nbCharacters the amount of characters for the token to be created.
     * @return a string token of [ncCharacters] characters.
     */
    String createStringToken(final String availableCharacters, final int nbCharacters);
}
