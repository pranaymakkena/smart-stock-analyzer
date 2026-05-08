package com.stockanalyzer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalyzer.dto.response.ApiResponse;
import com.stockanalyzer.dto.response.StockResponse;
import com.stockanalyzer.entity.Watchlist;
import com.stockanalyzer.service.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Watchlist>>> getWatchlists(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            watchlistService.getUserWatchlists(userDetails.getUsername())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Watchlist>> createWatchlist(
            @RequestParam String name,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Watchlist created",
            watchlistService.createWatchlist(name, userDetails.getUsername())));
    }

    @PostMapping("/{id}/stocks")
    public ResponseEntity<ApiResponse<Watchlist>> addStock(
            @PathVariable Long id,
            @RequestParam String symbol,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Stock added to watchlist",
            watchlistService.addStockToWatchlist(id, symbol, userDetails.getUsername())));
    }

    @DeleteMapping("/{id}/stocks/{symbol}")
    public ResponseEntity<ApiResponse<Watchlist>> removeStock(
            @PathVariable Long id,
            @PathVariable String symbol,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Stock removed from watchlist",
            watchlistService.removeStockFromWatchlist(id, symbol, userDetails.getUsername())));
    }

    @GetMapping("/{id}/stocks")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getWatchlistStocks(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            watchlistService.getWatchlistStocks(id, userDetails.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteWatchlist(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        watchlistService.deleteWatchlist(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Watchlist deleted", null));
    }
}
