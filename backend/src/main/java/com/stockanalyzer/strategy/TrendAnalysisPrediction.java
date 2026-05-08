package com.stockanalyzer.strategy;

import com.stockanalyzer.dto.response.PredictionResult;
import com.stockanalyzer.entity.StockHistory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * Trend Analysis prediction strategy using MACD and Bollinger Bands.
 * Demonstrates POLYMORPHISM — implements PredictionStrategy.
 *
 * Indicators:
 *  - MACD (12-day EMA - 26-day EMA)
 *  - Bollinger Bands (20-day MA ± 2σ)
 *  - Volume trend
 */
@Component
public class TrendAnalysisPrediction implements PredictionStrategy {

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    @Override
    public PredictionResult predict(List<StockHistory> history) {
        if (history == null || history.size() < 26) {
            return PredictionResult.builder()
                    .strategyName(getStrategyName())
                    .trend("SIDEWAYS").confidence(BigDecimal.valueOf(30))
                    .recommendation("HOLD")
                    .analysis("Insufficient data for Trend Analysis (need 26+ data points).")
                    .build();
        }

        BigDecimal ema12 = calculateEMA(history, 12);
        BigDecimal ema26 = calculateEMA(history, 26);
        BigDecimal macd  = ema12.subtract(ema26);

        BigDecimal[] bollinger = calculateBollinger(history, 20);
        BigDecimal bbUpper = bollinger[0];
        BigDecimal bbMiddle = bollinger[1];
        BigDecimal bbLower = bollinger[2];

        BigDecimal currentPrice = history.get(history.size() - 1).getClose();

        // Volume trend: compare last 5 days avg vs previous 5 days avg
        boolean volumeIncreasing = isVolumeIncreasing(history);

        String trend;
        String recommendation;
        BigDecimal confidence;
        String analysis;

        boolean macdBullish = macd.compareTo(BigDecimal.ZERO) > 0;
        boolean priceAboveMiddle = currentPrice.compareTo(bbMiddle) > 0;
        boolean nearUpperBand = currentPrice.compareTo(bbUpper.multiply(BigDecimal.valueOf(0.97))) > 0;
        boolean nearLowerBand = currentPrice.compareTo(bbLower.multiply(BigDecimal.valueOf(1.03))) < 0;

        if (macdBullish && priceAboveMiddle && volumeIncreasing) {
            trend = "UPTREND";
            recommendation = nearUpperBand ? "HOLD" : "BUY";
            confidence = BigDecimal.valueOf(78);
            analysis = String.format(
                "MACD positive (%.4f) with price above Bollinger midline. " +
                "Volume increasing confirms bullish momentum. BB: [%.2f - %.2f].",
                macd, bbLower, bbUpper
            );
        } else if (!macdBullish && !priceAboveMiddle && !volumeIncreasing) {
            trend = "DOWNTREND";
            recommendation = nearLowerBand ? "HOLD" : "SELL";
            confidence = BigDecimal.valueOf(74);
            analysis = String.format(
                "MACD negative (%.4f) with price below Bollinger midline. " +
                "Volume declining confirms bearish momentum. BB: [%.2f - %.2f].",
                macd, bbLower, bbUpper
            );
        } else {
            trend = "SIDEWAYS";
            recommendation = "HOLD";
            confidence = BigDecimal.valueOf(60);
            analysis = String.format(
                "Mixed signals: MACD=%.4f, Price vs BB midline: %s. " +
                "Volume trend: %s. Awaiting clearer direction.",
                macd, priceAboveMiddle ? "above" : "below",
                volumeIncreasing ? "increasing" : "decreasing"
            );
        }

        BigDecimal predictedPrice = currentPrice.add(
            macd.multiply(BigDecimal.valueOf(0.3), MC)
        ).setScale(4, RoundingMode.HALF_UP);

        return PredictionResult.builder()
                .strategyName(getStrategyName())
                .trend(trend)
                .predictedPrice(predictedPrice)
                .confidence(confidence)
                .macd(macd.setScale(4, RoundingMode.HALF_UP))
                .bollingerUpper(bbUpper.setScale(4, RoundingMode.HALF_UP))
                .bollingerLower(bbLower.setScale(4, RoundingMode.HALF_UP))
                .recommendation(recommendation)
                .analysis(analysis)
                .build();
    }

    @Override
    public String getStrategyName() {
        return "Trend Analysis";
    }

    private BigDecimal calculateEMA(List<StockHistory> history, int period) {
        if (history.size() < period) return history.get(history.size() - 1).getClose();

        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal ema = history.get(history.size() - period).getClose();

        for (int i = history.size() - period + 1; i < history.size(); i++) {
            BigDecimal price = history.get(i).getClose();
            ema = price.multiply(multiplier, MC)
                       .add(ema.multiply(BigDecimal.ONE.subtract(multiplier), MC));
        }
        return ema;
    }

    private BigDecimal[] calculateBollinger(List<StockHistory> history, int period) {
        int size = history.size();
        List<StockHistory> slice = history.subList(Math.max(0, size - period), size);

        BigDecimal sum = slice.stream().map(StockHistory::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal mean = sum.divide(BigDecimal.valueOf(slice.size()), MC);

        double variance = slice.stream()
                .mapToDouble(h -> Math.pow(h.getClose().subtract(mean).doubleValue(), 2))
                .average().orElse(0);
        BigDecimal stdDev = BigDecimal.valueOf(Math.sqrt(variance));

        BigDecimal upper = mean.add(stdDev.multiply(BigDecimal.valueOf(2)));
        BigDecimal lower = mean.subtract(stdDev.multiply(BigDecimal.valueOf(2)));
        return new BigDecimal[]{upper, mean, lower};
    }

    private boolean isVolumeIncreasing(List<StockHistory> history) {
        int size = history.size();
        if (size < 10) return false;

        double recent = history.subList(size - 5, size).stream()
                .mapToLong(h -> h.getVolume() != null ? h.getVolume() : 0L).average().orElse(0);
        double previous = history.subList(size - 10, size - 5).stream()
                .mapToLong(h -> h.getVolume() != null ? h.getVolume() : 0L).average().orElse(0);
        return recent > previous;
    }
}
