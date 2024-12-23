package com.project.urlshortener.configuration;

import com.project.urlshortener.configuration.properties.UrlShortenerProperties;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Additional Spring Boot configuration for the url-shortener application.
 */
@Configuration
@EnableRetry
@EnableConfigurationProperties(UrlShortenerProperties.class)
@EnableEncryptableProperties
public class UrlShortenerConfiguration implements WebMvcConfigurer {

	private static final String HTTP_LOCAL_CHANGE_PARAMETER = "lang";

	/**
	 * Access to the messages.properties and its internationalized variants.
	 *
	 * @return ResourceBundleMessageSource MessageSource implementation that accesses resource bundles using specified basenames.
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		var source = new ResourceBundleMessageSource();
		source.setBasenames("messages");
		source.setUseCodeAsDefaultMessage(true);

		return source;
	}

	/**
	 * Access to the active locale for the application.<br/>
	 * The default locale is Locale.ENGLISH.
	 *
	 * @return LocaleResolver Web-based locale resolution strategies.
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	/**
	 * Access to a URL validation tool from apache commons-validator.<br/>
	 * Default mode : UrlValidator.ALLOW_LOCAL_URLS.
	 *
	 * @return UrlValidator url validation routines.
	 */
	@Bean
	public UrlValidator urlValidator() {
		return new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
	}

	/**
	 * Declares an interceptor that detects locale change on the server.<br/>
	 * The locale can be changed when an endpoint is called with ?lang= with a Locale code.<br/>
	 * Ex. : ?lang=fr
	 *
	 * @return LocaleChangeInterceptor interceptor that allows for changing the current locale on every request, via a configurable request parameter.
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName(HTTP_LOCAL_CHANGE_PARAMETER);
		return lci;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

}
