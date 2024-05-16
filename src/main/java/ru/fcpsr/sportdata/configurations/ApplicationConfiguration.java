package ru.fcpsr.sportdata.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName("session-sport-data");
        resolver.setCookieMaxAge(Duration.ofHours(24));
        return resolver;
    }
}
