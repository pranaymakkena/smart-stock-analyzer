package com.stockanalyzer.config;

import com.stockanalyzer.repository.StockRepository;
import com.stockanalyzer.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Simulates real-time price updates via WebSocket.
 * In production, replace with Alpha Vantage / Yahoo Finance API calls.
 */
@Component
public class StockPriceScheduler {

    private static final Logger log = LoggerFactory.getLogger(StockPriceScheduler.class);

    private final StockRepository stockRepository;
    private final StockService stockService;
    private final SimpMessagingTemplate messagingTemplate;

    public StockPriceScheduler(StockRepository stockRepository, StockService stockService,
                                SimpMessagingTemplate messagingTemplate) {
        this.stockRepository = stockRepository;
        this.stockService = stockService;
        this.messagingTemplate = messagingTemplate;
    }

    // Refresh all stock prices every 30 seconds
    @Scheduled(fixedDelay = 30000)
    public void refreshAllPrices() {
        stockRepository.findAll().forEach(stock -> {
            try {
                stockService.refreshStockData(stock.getSymbol());
                // Broadcast updated price via WebSocket
                messagingTemplate.convertAndSend(
                    "/topic/stocks/" + stock.getSymbol(),
                    stockService.getStockBySymbol(stock.getSymbol())
                );
            } catch (Exception e) {
                log.debug("Error refreshing {}: {}", stock.getSymbol(), e.getMessage());
            }
        });
    }
}
