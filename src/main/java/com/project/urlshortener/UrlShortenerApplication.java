package com.project.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main SPRING-BOOT application for url-shortener.
 */
@SpringBootApplication
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
