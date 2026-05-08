package com.stockanalyzer.service;

import com.stockanalyzer.dto.response.PredictionResult;
import com.stockanalyzer.dto.response.RiskAnalysisResponse;

import java.util.List;
import java.util.Map;

/**
 * Prediction service interface — demonstrates ABSTRACTION.
 * Uses Strategy pattern for different prediction algorithms.
 */
public interface PredictionService {
    PredictionResult predictStock(String symbol, String strategy);
    List<PredictionResult> predictAllStrategies(String symbol);
    RiskAnalysisResponse analyzeRisk(String symbol);
    Map<String, Object> getRecommendations(String email);
    List<Map<String, Object>> getTechnicalIndicators(String symbol);
}
