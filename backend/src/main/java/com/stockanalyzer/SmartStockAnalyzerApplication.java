package com.stockanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.stockanalyzer.config.StartupLogger;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SmartStockAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SmartStockAnalyzerApplication.class);
        app.addListeners(new StartupLogger());
        app.run(args);
    }
}
