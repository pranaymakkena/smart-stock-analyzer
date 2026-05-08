package com.stockanalyzer.service.impl;

import com.stockanalyzer.dto.response.MarketSummaryResponse;
import com.stockanalyzer.dto.response.StockResponse;
import com.stockanalyzer.entity.Stock;
import com.stockanalyzer.entity.StockHistory;
import com.stockanalyzer.entity.enums.Sector;
import com.stockanalyzer.repository.StockHistoryRepository;
import com.stockanalyzer.repository.StockRepository;
import com.stockanalyzer.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    private final StockRepository stockRepository;
    private final StockHistoryRepository historyRepository;

    public StockServiceImpl(StockRepository stockRepository, StockHistoryRepository historyRepository) {
        this.stockRepository = stockRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    @Cacheable(value = "stocks", key = "#symbol")
    public StockResponse getStockBySymbol(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));
        return mapToResponse(stock);
    }

    @Override
    public List<StockResponse> searchStocks(String query) {
        return stockRepository.searchStocks(query).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> getStocksBySector(String sector) {
        try {
            Sector s = Sector.valueOf(sector.toUpperCase());
            return stockRepository.findBySector(s).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public MarketSummaryResponse getMarketSummary() {
        List<Stock> allStocks = stockRepository.findAll();

        List<StockResponse> gainers = stockRepository.findTopGainers().stream()
                .limit(5).map(this::mapToResponse).collect(Collectors.toList());
        List<StockResponse> losers = stockRepository.findTopLosers().stream()
                .limit(5).map(this::mapToResponse).collect(Collectors.toList());
        List<StockResponse> active = stockRepository.findMostActive().stream()
                .limit(5).map(this::mapToResponse).collect(Collectors.toList());

        Map<String, BigDecimal> sectorPerf = new HashMap<>();
        for (Sector sector : Sector.values()) {
            List<Stock> sectorStocks = stockRepository.findBySector(sector);
            if (!sectorStocks.isEmpty()) {
                BigDecimal avgChange = sectorStocks.stream()
                        .filter(s -> s.getChangePercent() != null)
                        .map(Stock::getChangePercent)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(sectorStocks.size()), 2, RoundingMode.HALF_UP);
                sectorPerf.put(sector.name(), avgChange);
            }
        }

        long gainersCount = allStocks.stream()
                .filter(s -> s.getChangePercent() != null && s.getChangePercent().compareTo(BigDecimal.ZERO) > 0)
                .count();

        return MarketSummaryResponse.builder()
                .topGainers(gainers)
                .topLosers(losers)
                .mostActive(active)
                .sectorPerformance(sectorPerf)
                .totalStocksTracked(allStocks.size())
                .gainersCount(gainersCount)
                .losersCount(allStocks.size() - gainersCount)
                .build();
    }

    @Override
    public List<StockHistory> getStockHistory(String symbol, String period) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        LocalDate to = LocalDate.now();
        LocalDate from = switch (period.toLowerCase()) {
            case "1w"  -> to.minusWeeks(1);
            case "1m"  -> to.minusMonths(1);
            case "3m"  -> to.minusMonths(3);
            case "6m"  -> to.minusMonths(6);
            case "1y"  -> to.minusYears(1);
            case "5y"  -> to.minusYears(5);
            default    -> to.minusMonths(1);
        };

        return historyRepository.findByStockAndDateBetweenOrderByDateAsc(stock, from, to);
    }

    @Override
    public Map<String, Object> getSectorPerformance() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Sector sector : Sector.values()) {
            List<Stock> stocks = stockRepository.findBySector(sector);
            if (!stocks.isEmpty()) {
                Map<String, Object> sectorData = new HashMap<>();
                sectorData.put("stockCount", stocks.size());
                BigDecimal avgChange = stocks.stream()
                        .filter(s -> s.getChangePercent() != null)
                        .map(Stock::getChangePercent)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(Math.max(stocks.size(), 1)), 2, RoundingMode.HALF_UP);
                sectorData.put("avgChangePercent", avgChange);
                sectorData.put("stocks", stocks.stream().map(this::mapToResponse).collect(Collectors.toList()));
                result.put(sector.name(), sectorData);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void refreshStockData(String symbol) {
        // In production: call Alpha Vantage / Yahoo Finance API
        // For demo: simulate price movement
        stockRepository.findBySymbol(symbol.toUpperCase()).ifPresent(stock -> {
            if (stock.getCurrentPrice() != null) {
                double change = (Math.random() - 0.48) * 2; // slight upward bias
                BigDecimal newPrice = stock.getCurrentPrice()
                        .multiply(BigDecimal.valueOf(1 + change / 100))
                        .setScale(4, RoundingMode.HALF_UP);
                BigDecimal priceChange = newPrice.subtract(stock.getPreviousClose() != null
                        ? stock.getPreviousClose() : stock.getCurrentPrice());
                BigDecimal changePct = stock.getPreviousClose() != null && stock.getPreviousClose().compareTo(BigDecimal.ZERO) > 0
                        ? priceChange.divide(stock.getPreviousClose(), 4, RoundingMode.HALF_UP)
                                     .multiply(BigDecimal.valueOf(100))
                        : BigDecimal.ZERO;

                stock.setCurrentPrice(newPrice);
                stock.setChange(priceChange.setScale(4, RoundingMode.HALF_UP));
                stock.setChangePercent(changePct.setScale(2, RoundingMode.HALF_UP));
                if (newPrice.compareTo(stock.getDayHigh() != null ? stock.getDayHigh() : BigDecimal.ZERO) > 0)
                    stock.setDayHigh(newPrice);
                if (stock.getDayLow() == null || newPrice.compareTo(stock.getDayLow()) < 0)
                    stock.setDayLow(newPrice);
                stockRepository.save(stock);
            }
        });
    }

    @Override
    public List<StockResponse> getTopGainers(int limit) {
        return stockRepository.findTopGainers().stream()
                .limit(limit).map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> getTopLosers(int limit) {
        return stockRepository.findTopLosers().stream()
                .limit(limit).map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> getMostActive(int limit) {
        return stockRepository.findMostActive().stream()
                .limit(limit).map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Stock findOrCreateStock(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseGet(() -> {
                    Stock s = new Stock(symbol.toUpperCase(), symbol.toUpperCase(), Sector.OTHER);
                    return stockRepository.save(s);
                });
    }

    public StockResponse mapToResponse(Stock stock) {
        return StockResponse.builder()
                .id(stock.getId())
                .symbol(stock.getSymbol())
                .companyName(stock.getCompanyName())
                .sector(stock.getSector() != null ? stock.getSector().name() : null)
                .currentPrice(stock.getCurrentPrice())
                .openPrice(stock.getOpenPrice())
                .dayHigh(stock.getDayHigh())
                .dayLow(stock.getDayLow())
                .previousClose(stock.getPreviousClose())
                .change(stock.getChange())
                .changePercent(stock.getChangePercent())
                .volume(stock.getVolume())
                .avgVolume(stock.getAvgVolume())
                .marketCap(stock.getMarketCap())
                .peRatio(stock.getPeRatio())
                .fiftyTwoWeekHigh(stock.getFiftyTwoWeekHigh())
                .fiftyTwoWeekLow(stock.getFiftyTwoWeekLow())
                .exchange(stock.getExchange())
                .currency(stock.getCurrency())
                .lastUpdated(stock.getLastUpdated())
                .build();
    }
}
