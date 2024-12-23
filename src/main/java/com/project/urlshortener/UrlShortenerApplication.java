package com.project.urlshortener;

import com.project.urlshortener.configuration.properties.UrlShortenerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main SPRING-BOOT application for url-shortener.
 */
@SpringBootApplication
@EnableConfigurationProperties(UrlShortenerProperties.class)
public class UrlShortenerApplication {

	/**
	 * Main entry point for the spring boot url-shortener application.
	 *
	 * @param args main java command line arguments.
	 */
	public static void main(final String[] args) {
		SpringApplication.run(UrlShortenerApplication.class, args);
	}

}
