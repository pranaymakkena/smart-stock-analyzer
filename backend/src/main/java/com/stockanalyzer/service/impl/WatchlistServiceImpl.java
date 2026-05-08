package com.stockanalyzer.service.impl;

import com.stockanalyzer.dto.response.StockResponse;
import com.stockanalyzer.entity.Stock;
import com.stockanalyzer.entity.User;
import com.stockanalyzer.entity.Watchlist;
import com.stockanalyzer.repository.StockRepository;
import com.stockanalyzer.repository.UserRepository;
import com.stockanalyzer.repository.WatchlistRepository;
import com.stockanalyzer.service.WatchlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final StockServiceImpl stockService;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, UserRepository userRepository,
                                 StockRepository stockRepository, StockServiceImpl stockService) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    @Override
    public List<Watchlist> getUserWatchlists(String email) {
        User user = getUser(email);
        return watchlistRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Watchlist createWatchlist(String name, String email) {
        User user = getUser(email);
        if (watchlistRepository.existsByUserAndName(user, name)) {
            throw new RuntimeException("Watchlist '" + name + "' already exists");
        }
        return watchlistRepository.save(new Watchlist(user, name));
    }

    @Override
    @Transactional
    public Watchlist addStockToWatchlist(Long watchlistId, String symbol, String email) {
        User user = getUser(email);
        Watchlist watchlist = watchlistRepository.findByIdAndUser(watchlistId, user)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));

        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        boolean alreadyAdded = watchlist.getStocks().stream()
                .anyMatch(s -> s.getSymbol().equals(stock.getSymbol()));
        if (!alreadyAdded) {
            watchlist.getStocks().add(stock);
            watchlistRepository.save(watchlist);
        }
        return watchlist;
    }

    @Override
    @Transactional
    public Watchlist removeStockFromWatchlist(Long watchlistId, String symbol, String email) {
        User user = getUser(email);
        Watchlist watchlist = watchlistRepository.findByIdAndUser(watchlistId, user)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));

        watchlist.getStocks().removeIf(s -> s.getSymbol().equalsIgnoreCase(symbol));
        return watchlistRepository.save(watchlist);
    }

    @Override
    @Transactional
    public void deleteWatchlist(Long watchlistId, String email) {
        User user = getUser(email);
        Watchlist watchlist = watchlistRepository.findByIdAndUser(watchlistId, user)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));
        watchlistRepository.delete(watchlist);
    }

    @Override
    public List<StockResponse> getWatchlistStocks(Long watchlistId, String email) {
        User user = getUser(email);
        Watchlist watchlist = watchlistRepository.findByIdAndUser(watchlistId, user)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));
        return watchlist.getStocks().stream()
                .map(stockService::mapToResponse)
                .collect(Collectors.toList());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
