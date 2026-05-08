package com.stockanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { MailSenderAutoConfiguration.class })
@EnableCaching
@EnableScheduling
public class SmartStockAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartStockAnalyzerApplication.class, args);
    }
}
