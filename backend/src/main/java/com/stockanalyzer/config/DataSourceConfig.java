package com.stockanalyzer.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Manually configures the DataSource so we can normalise the DATABASE_URL
 * that Render provides (postgres://...) into the JDBC format Spring needs
 * (jdbc:postgresql://...).
 *
 * DataSourceAutoConfiguration is excluded in SmartStockAnalyzerApplication
 * so Spring Boot doesn't try to wire its own conflicting DataSource bean.
 */
@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        String url = resolveJdbcUrl();

        if (url.startsWith("jdbc:postgresql")) {
            // PostgreSQL — credentials are embedded in the URL by Render
            return DataSourceBuilder.create()
                    .url(url)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }

        // H2 — local development fallback
        return DataSourceBuilder.create()
                .url(url)
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }

    private String resolveJdbcUrl() {
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return "jdbc:h2:mem:stockanalyzerdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        }
        // Render gives postgres:// — convert to jdbc:postgresql://
        if (databaseUrl.startsWith("postgres://")) {
            return databaseUrl.replace("postgres://", "jdbc:postgresql://");
        }
        // Already jdbc:postgresql:// or jdbc:h2://
        return databaseUrl;
    }
}
