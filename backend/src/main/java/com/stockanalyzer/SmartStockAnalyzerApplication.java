package com.stockanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.stockanalyzer.config.DatabaseUrlNormalizer;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SmartStockAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SmartStockAnalyzerApplication.class);
        // Normalises postgres:// → jdbc:postgresql:// before any bean is created
        app.addInitializers(new DatabaseUrlNormalizer());
        app.run(args);
    }
}
