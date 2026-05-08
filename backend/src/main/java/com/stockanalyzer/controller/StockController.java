package com.stockanalyzer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalyzer.dto.response.ApiResponse;
import com.stockanalyzer.dto.response.MarketSummaryResponse;
import com.stockanalyzer.dto.response.StockResponse;
import com.stockanalyzer.entity.StockHistory;
import com.stockanalyzer.service.StockService;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse>>> getAllStocks() {
        return ResponseEntity.ok(ApiResponse.success(stockService.getAllStocks()));
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<ApiResponse<StockResponse>> getStock(@PathVariable String symbol) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getStockBySymbol(symbol)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StockResponse>>> searchStocks(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(stockService.searchStocks(q)));
    }

    @GetMapping("/sector/{sector}")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStocksBySector(@PathVariable String sector) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getStocksBySector(sector)));
    }

    @GetMapping("/market/summary")
    public ResponseEntity<ApiResponse<MarketSummaryResponse>> getMarketSummary() {
        return ResponseEntity.ok(ApiResponse.success(stockService.getMarketSummary()));
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<ApiResponse<List<StockHistory>>> getHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1m") String period) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getStockHistory(symbol, period)));
    }

    @GetMapping("/market/sectors")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSectorPerformance() {
        return ResponseEntity.ok(ApiResponse.success(stockService.getSectorPerformance()));
    }

    @GetMapping("/market/gainers")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getTopGainers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getTopGainers(limit)));
    }

    @GetMapping("/market/losers")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getTopLosers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getTopLosers(limit)));
    }

    @GetMapping("/market/active")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getMostActive(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getMostActive(limit)));
    }

    @PostMapping("/{symbol}/refresh")
    public ResponseEntity<ApiResponse<String>> refreshStock(@PathVariable String symbol) {
        stockService.refreshStockData(symbol);
        return ResponseEntity.ok(ApiResponse.success("Stock data refreshed", symbol));
    }
}
