package com.stockanalyzer.strategy;

import com.stockanalyzer.dto.response.PredictionResult;
import com.stockanalyzer.entity.StockHistory;

import java.util.List;

/**
 * Strategy interface — demonstrates ABSTRACTION and POLYMORPHISM.
 * Different prediction algorithms implement this interface.
 */
public interface PredictionStrategy {

    /**
     * Analyze historical data and return a prediction result.
     *
     * @param history list of historical OHLCV data (most recent last)
     * @return prediction result with trend, confidence, and indicators
     */
    PredictionResult predict(List<StockHistory> history);

    /**
     * Returns the name of this strategy.
     */
    String getStrategyName();
}
