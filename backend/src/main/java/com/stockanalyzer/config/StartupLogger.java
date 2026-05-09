package com.stockanalyzer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

/**
 * Logs the resolved datasource URL at the earliest possible startup point.
 * Remove after debugging.
 */
public class StartupLogger
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Environment env = event.getEnvironment();
        String dbUrl      = env.getProperty("DATABASE_URL", "NOT SET");
        String dsUrl      = env.getProperty("spring.datasource.url", "NOT SET");
        String dbUser     = env.getProperty("DB_USERNAME", "NOT SET");

        // Mask password in URL for safe logging
        String safeDbUrl = dbUrl.replaceAll(":[^:@]+@", ":***@");
        String safeDsUrl = dsUrl.replaceAll(":[^:@]+@", ":***@");

        System.out.println("========================================");
        System.out.println("DATABASE_URL        = " + safeDbUrl);
        System.out.println("spring.datasource.url = " + safeDsUrl);
        System.out.println("DB_USERNAME         = " + dbUser);
        System.out.println("========================================");
    }
}
