package com.stockanalyzer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalyzer.dto.request.TradeRequest;
import com.stockanalyzer.dto.response.ApiResponse;
import com.stockanalyzer.dto.response.PortfolioResponse;
import com.stockanalyzer.dto.response.TransactionResponse;
import com.stockanalyzer.service.PortfolioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfolios(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            portfolioService.getUserPortfolios(userDetails.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            portfolioService.getPortfolioById(id, userDetails.getUsername())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(
            @RequestParam String name,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Portfolio created",
            portfolioService.createPortfolio(name, userDetails.getUsername())));
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<PortfolioResponse>> buyStock(
            @Valid @RequestBody TradeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Stock purchased successfully",
            portfolioService.buyStock(request, userDetails.getUsername())));
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<PortfolioResponse>> sellStock(
            @Valid @RequestBody TradeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Stock sold successfully",
            portfolioService.sellStock(request, userDetails.getUsername())));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            portfolioService.getTransactionHistory(userDetails.getUsername())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshValues(
            @AuthenticationPrincipal UserDetails userDetails) {
        portfolioService.refreshPortfolioValues(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Portfolio values refreshed", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        portfolioService.deletePortfolio(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Portfolio deleted", null));
    }
}
