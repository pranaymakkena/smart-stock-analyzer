package com.stockanalyzer.strategy;

import com.stockanalyzer.dto.response.PredictionResult;
import com.stockanalyzer.entity.StockHistory;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Linear Regression prediction strategy.
 * Demonstrates POLYMORPHISM — implements PredictionStrategy.
 *
 * Uses Apache Commons Math SimpleRegression to fit a line
 * through closing prices and extrapolate the next value.
 */
@Component
public class LinearRegressionPrediction implements PredictionStrategy {

    @Override
    public PredictionResult predict(List<StockHistory> history) {
        if (history == null || history.size() < 10) {
            return PredictionResult.builder()
                    .strategyName(getStrategyName())
                    .trend("SIDEWAYS").confidence(BigDecimal.valueOf(30))
                    .recommendation("HOLD")
                    .analysis("Insufficient data for Linear Regression analysis.")
                    .build();
        }

        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < history.size(); i++) {
            regression.addData(i, history.get(i).getClose().doubleValue());
        }

        double slope = regression.getSlope();
        double rSquared = regression.getRSquare();
        double nextX = history.size();
        double predictedValue = regression.predict(nextX);

        BigDecimal currentPrice = history.get(history.size() - 1).getClose();
        BigDecimal predictedPrice = BigDecimal.valueOf(Math.max(predictedValue, 0.01))
                .setScale(4, RoundingMode.HALF_UP);

        // Confidence based on R² value
        BigDecimal confidence = BigDecimal.valueOf(rSquared * 100).setScale(2, RoundingMode.HALF_UP);

        String trend;
        String recommendation;
        String analysis;

        double changePercent = ((predictedValue - currentPrice.doubleValue()) / currentPrice.doubleValue()) * 100;

        if (slope > 0 && changePercent > 1.0) {
            trend = "UPTREND";
            recommendation = "BUY";
            analysis = String.format(
                "Linear regression slope: +%.4f. Predicted price: %.2f (%.2f%% increase). " +
                "R² = %.2f indicates %s fit.",
                slope, predictedValue, changePercent, rSquared,
                rSquared > 0.7 ? "strong" : "moderate"
            );
        } else if (slope < 0 && changePercent < -1.0) {
            trend = "DOWNTREND";
            recommendation = "SELL";
            analysis = String.format(
                "Linear regression slope: %.4f. Predicted price: %.2f (%.2f%% decrease). " +
                "R² = %.2f indicates %s fit.",
                slope, predictedValue, changePercent, rSquared,
                rSquared > 0.7 ? "strong" : "moderate"
            );
        } else {
            trend = "SIDEWAYS";
            recommendation = "HOLD";
            analysis = String.format(
                "Linear regression slope near zero (%.4f). Price expected to remain stable around %.2f. " +
                "R² = %.2f.",
                slope, predictedValue, rSquared
            );
        }

        return PredictionResult.builder()
                .strategyName(getStrategyName())
                .trend(trend)
                .predictedPrice(predictedPrice)
                .confidence(confidence)
                .recommendation(recommendation)
                .analysis(analysis)
                .build();
    }

    @Override
    public String getStrategyName() {
        return "Linear Regression";
    }
}
