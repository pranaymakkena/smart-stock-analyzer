package com.stockanalyzer.service;

import com.stockanalyzer.dto.response.StockResponse;
import com.stockanalyzer.entity.Watchlist;

import java.util.List;

public interface WatchlistService {
    List<Watchlist> getUserWatchlists(String email);
    Watchlist createWatchlist(String name, String email);
    Watchlist addStockToWatchlist(Long watchlistId, String symbol, String email);
    Watchlist removeStockFromWatchlist(Long watchlistId, String symbol, String email);
    void deleteWatchlist(Long watchlistId, String email);
    List<StockResponse> getWatchlistStocks(Long watchlistId, String email);
}
