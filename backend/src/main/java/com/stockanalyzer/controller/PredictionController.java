package com.stockanalyzer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalyzer.dto.response.ApiResponse;
import com.stockanalyzer.dto.response.PredictionResult;
import com.stockanalyzer.dto.response.RiskAnalysisResponse;
import com.stockanalyzer.service.PredictionService;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<ApiResponse<PredictionResult>> predict(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "moving-average") String strategy) {
        return ResponseEntity.ok(ApiResponse.success(
            predictionService.predictStock(symbol, strategy)));
    }

    @GetMapping("/{symbol}/all")
    public ResponseEntity<ApiResponse<List<PredictionResult>>> predictAll(
            @PathVariable String symbol) {
        return ResponseEntity.ok(ApiResponse.success(
            predictionService.predictAllStrategies(symbol)));
    }

    @GetMapping("/{symbol}/risk")
    public ResponseEntity<ApiResponse<RiskAnalysisResponse>> analyzeRisk(
            @PathVariable String symbol) {
        return ResponseEntity.ok(ApiResponse.success(
            predictionService.analyzeRisk(symbol)));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            predictionService.getRecommendations(userDetails.getUsername())));
    }

    @GetMapping("/{symbol}/indicators")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTechnicalIndicators(
            @PathVariable String symbol) {
        return ResponseEntity.ok(ApiResponse.success(
            predictionService.getTechnicalIndicators(symbol)));
    }
}
