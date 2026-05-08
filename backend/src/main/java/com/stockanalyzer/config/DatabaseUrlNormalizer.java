package com.stockanalyzer.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Runs before the Spring context is fully built.
 *
 * Render provides DATABASE_URL as:  postgres://user:pass@host:5432/db
 * Spring/Hibernate needs:           jdbc:postgresql://user:pass@host:5432/db
 *
 * This initializer reads DATABASE_URL, converts it, then injects it as
 * spring.datasource.url so all of Spring Boot's JPA autoconfiguration
 * works normally without any exclusions or custom DataSource beans.
 */
public class DatabaseUrlNormalizer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment env = context.getEnvironment();

        String rawUrl = env.getProperty("DATABASE_URL", "");

        if (rawUrl.isBlank()) {
            // No DATABASE_URL — local dev, H2 fallback already set in properties
            return;
        }

        String jdbcUrl = toJdbcUrl(rawUrl);

        // Inject as highest-priority property source so it overrides application.properties
        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", jdbcUrl);
        props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");

        env.getPropertySources().addFirst(
            new MapPropertySource("renderDatabaseUrl", props)
        );
    }

    private String toJdbcUrl(String url) {
        if (url.startsWith("jdbc:")) {
            return url; // already correct
        }
        if (url.startsWith("postgres://")) {
            return url.replace("postgres://", "jdbc:postgresql://");
        }
        if (url.startsWith("postgresql://")) {
            return "jdbc:" + url;
        }
        return url;
    }
}
