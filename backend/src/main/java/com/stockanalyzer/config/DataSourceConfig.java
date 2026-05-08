package com.stockanalyzer.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Normalises the DATABASE_URL that Render (and Heroku) provide.
 *
 * Render gives:  postgres://user:pass@host:5432/dbname
 * Spring needs:  jdbc:postgresql://user:pass@host:5432/dbname
 *
 * This bean handles both formats so the app starts regardless of
 * which style is set in the environment variable.
 */
@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${DB_USERNAME:sa}")
    private String username;

    @Value("${DB_PASSWORD:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        String url = resolveJdbcUrl();

        // Pick the right driver from the URL
        String driver = url.startsWith("jdbc:postgresql") ? "org.postgresql.Driver"
                      : url.startsWith("jdbc:h2")         ? "org.h2.Driver"
                      : "org.h2.Driver";

        // For PostgreSQL, username/password come from the URL itself —
        // no need to pass them separately (Render embeds them in the URL)
        if (url.startsWith("jdbc:postgresql")) {
            return DataSourceBuilder.create()
                    .url(url)
                    .driverClassName(driver)
                    .build();
        }

        // H2 local dev
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driver)
                .build();
    }

    private String resolveJdbcUrl() {
        if (databaseUrl == null || databaseUrl.isBlank()) {
            // No DATABASE_URL set — use H2 for local development
            return "jdbc:h2:mem:stockanalyzerdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        }

        // Already correct format
        if (databaseUrl.startsWith("jdbc:")) {
            return databaseUrl;
        }

        // Render/Heroku style: postgres:// → jdbc:postgresql://
        if (databaseUrl.startsWith("postgres://")) {
            return databaseUrl.replace("postgres://", "jdbc:postgresql://");
        }

        return databaseUrl;
    }
}
