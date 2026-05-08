package com.stockanalyzer.service;

import com.stockanalyzer.dto.response.MarketSummaryResponse;
import com.stockanalyzer.dto.response.StockResponse;
import com.stockanalyzer.entity.Stock;
import com.stockanalyzer.entity.StockHistory;

import java.util.List;
import java.util.Map;

/**
 * Stock service interface — demonstrates ABSTRACTION.
 */
public interface StockService {
    StockResponse getStockBySymbol(String symbol);
    List<StockResponse> searchStocks(String query);
    List<StockResponse> getAllStocks();
    List<StockResponse> getStocksBySector(String sector);
    MarketSummaryResponse getMarketSummary();
    List<StockHistory> getStockHistory(String symbol, String period);
    Map<String, Object> getSectorPerformance();
    void refreshStockData(String symbol);
    List<StockResponse> getTopGainers(int limit);
    List<StockResponse> getTopLosers(int limit);
    List<StockResponse> getMostActive(int limit);
    Stock findOrCreateStock(String symbol);
}
