package com.stockanalyzer.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalyzer.dto.response.ApiResponse;
import com.stockanalyzer.dto.response.LeaderboardEntry;
import com.stockanalyzer.dto.response.UserResponse;
import com.stockanalyzer.entity.User;
import com.stockanalyzer.repository.TransactionRepository;
import com.stockanalyzer.repository.UserRepository;
import com.stockanalyzer.service.StockService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final StockService stockService;

    public AdminController(UserRepository userRepository, TransactionRepository transactionRepository,
                           StockService stockService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.stockService = stockService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(this::mapUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getLeaderboard() {
        List<User> users = userRepository.findLeaderboard();
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            long trades = transactionRepository.countByUser(user);
            BigDecimal invested = user.getTotalInvested() != null ? user.getTotalInvested() : BigDecimal.ZERO;
            BigDecimal profit = user.getTotalProfit() != null ? user.getTotalProfit() : BigDecimal.ZERO;
            BigDecimal profitPct = invested.compareTo(BigDecimal.ZERO) > 0
                    ? profit.divide(invested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            leaderboard.add(LeaderboardEntry.builder()
                    .rank(i + 1)
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .totalProfit(profit)
                    .profitPercent(profitPct)
                    .portfolioValue(user.getVirtualBalance().add(invested))
                    .totalTrades(trades)
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    @PostMapping("/stocks/refresh-all")
    public ResponseEntity<ApiResponse<String>> refreshAllStocks() {
        stockService.getAllStocks().forEach(s -> stockService.refreshStockData(s.getSymbol()));
        return ResponseEntity.ok(ApiResponse.success("All stocks refreshed", null));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getStats() {
        long totalUsers = userRepository.count();
        long totalStocks = stockService.getAllStocks().size();
        return ResponseEntity.ok(ApiResponse.success(
            java.util.Map.of("totalUsers", totalUsers, "totalStocks", totalStocks)
        ));
    }

    private UserResponse mapUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .virtualBalance(user.getVirtualBalance())
                .totalInvested(user.getTotalInvested())
                .totalProfit(user.getTotalProfit())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
