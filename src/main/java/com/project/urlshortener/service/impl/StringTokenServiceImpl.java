package com.project.urlshortener.service.impl;

import com.project.urlshortener.service.StringTokenService;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collector;

import static com.project.urlshortener.common.utils.ArgumentUtils.requireNonBlank;
import static com.project.urlshortener.common.utils.ArgumentUtils.requireStrictlyPositiveValue;

/**
 * Service to create tokens of random characters.<br/>
 * Implements StringTokenService.
 */
@Service
public class StringTokenServiceImpl implements StringTokenService {

    /**
     * Randomizer, used to create random sequence of characters.<br/>
     * Uses the InstanceStrong implementation from SecureRandom.
     */
    private final Random random;

    /**
     * Default constructor for StringTokenServiceImpl.
     * @throws NoSuchAlgorithmException if SecureRandom.getInstanceStrong() fails
     */
    public StringTokenServiceImpl() throws NoSuchAlgorithmException {
        this.random = SecureRandom.getInstanceStrong();
    }

    @Override
    public String createStringToken(final String availableCharacters, final int nbCharacters) {
        requireNonBlank(availableCharacters, "availableCharacters");
        requireStrictlyPositiveValue(nbCharacters, "nbCharacters");

        return random.ints(nbCharacters, 0, availableCharacters.length())
                    .mapToObj(availableCharacters::charAt)
                    .collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append,
                        StringBuilder::toString)
                    );
    }


}
