package com.stockanalyzer.strategy;

import com.stockanalyzer.dto.response.PredictionResult;
import com.stockanalyzer.entity.StockHistory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * Moving Average prediction strategy.
 * Demonstrates POLYMORPHISM — implements PredictionStrategy.
 *
 * Logic:
 *  - MA5 > MA20 > MA50 → Strong UPTREND
 *  - MA5 < MA20 < MA50 → Strong DOWNTREND
 *  - Otherwise → SIDEWAYS
 */
@Component
public class MovingAveragePrediction implements PredictionStrategy {

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    @Override
    public PredictionResult predict(List<StockHistory> history) {
        if (history == null || history.size() < 5) {
            return buildResult("SIDEWAYS", BigDecimal.ZERO, BigDecimal.valueOf(30),
                    "HOLD", "Insufficient data for Moving Average analysis.");
        }

        BigDecimal ma5  = calculateMA(history, 5);
        BigDecimal ma20 = history.size() >= 20 ? calculateMA(history, 20) : ma5;
        BigDecimal ma50 = history.size() >= 50 ? calculateMA(history, 50) : ma20;

        BigDecimal currentPrice = history.get(history.size() - 1).getClose();
        BigDecimal rsi = calculateRSI(history, 14);

        String trend;
        String recommendation;
        BigDecimal confidence;
        String analysis;

        if (ma5.compareTo(ma20) > 0 && ma20.compareTo(ma50) > 0) {
            trend = "UPTREND";
            recommendation = "BUY";
            confidence = BigDecimal.valueOf(75);
            analysis = String.format(
                "MA5 (%.2f) > MA20 (%.2f) > MA50 (%.2f). Bullish crossover detected. " +
                "RSI at %.1f suggests %s momentum.",
                ma5, ma20, ma50, rsi,
                rsi.compareTo(BigDecimal.valueOf(70)) > 0 ? "overbought" : "strong upward"
            );
        } else if (ma5.compareTo(ma20) < 0 && ma20.compareTo(ma50) < 0) {
            trend = "DOWNTREND";
            recommendation = "SELL";
            confidence = BigDecimal.valueOf(72);
            analysis = String.format(
                "MA5 (%.2f) < MA20 (%.2f) < MA50 (%.2f). Bearish crossover detected. " +
                "RSI at %.1f suggests %s momentum.",
                ma5, ma20, ma50, rsi,
                rsi.compareTo(BigDecimal.valueOf(30)) < 0 ? "oversold" : "downward"
            );
        } else {
            trend = "SIDEWAYS";
            recommendation = "HOLD";
            confidence = BigDecimal.valueOf(55);
            analysis = String.format(
                "Moving averages are converging (MA5: %.2f, MA20: %.2f, MA50: %.2f). " +
                "Market is consolidating. RSI: %.1f.",
                ma5, ma20, ma50, rsi
            );
        }

        // Predict next price using simple MA projection
        BigDecimal predictedPrice = currentPrice.add(
            currentPrice.multiply(ma5.subtract(ma20).divide(ma20, MC).multiply(BigDecimal.valueOf(0.5), MC))
        ).setScale(4, RoundingMode.HALF_UP);

        return PredictionResult.builder()
                .strategyName(getStrategyName())
                .trend(trend)
                .predictedPrice(predictedPrice)
                .confidence(confidence)
                .ma5(ma5.setScale(4, RoundingMode.HALF_UP))
                .ma20(ma20.setScale(4, RoundingMode.HALF_UP))
                .ma50(ma50.setScale(4, RoundingMode.HALF_UP))
                .rsi(rsi.setScale(2, RoundingMode.HALF_UP))
                .recommendation(recommendation)
                .analysis(analysis)
                .build();
    }

    @Override
    public String getStrategyName() {
        return "Moving Average";
    }

    private BigDecimal calculateMA(List<StockHistory> history, int period) {
        int size = history.size();
        List<StockHistory> slice = history.subList(Math.max(0, size - period), size);
        BigDecimal sum = slice.stream()
                .map(StockHistory::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(slice.size()), MC);
    }

    private BigDecimal calculateRSI(List<StockHistory> history, int period) {
        if (history.size() < period + 1) return BigDecimal.valueOf(50);

        int size = history.size();
        List<StockHistory> slice = history.subList(size - period - 1, size);

        double gains = 0, losses = 0;
        for (int i = 1; i < slice.size(); i++) {
            double change = slice.get(i).getClose().subtract(slice.get(i - 1).getClose()).doubleValue();
            if (change > 0) gains += change;
            else losses += Math.abs(change);
        }

        if (losses == 0) return BigDecimal.valueOf(100);
        double rs = gains / losses;
        double rsi = 100 - (100 / (1 + rs));
        return BigDecimal.valueOf(rsi);
    }

    private PredictionResult buildResult(String trend, BigDecimal price,
                                          BigDecimal confidence, String rec, String analysis) {
        return PredictionResult.builder()
                .strategyName(getStrategyName())
                .trend(trend).predictedPrice(price)
                .confidence(confidence).recommendation(rec).analysis(analysis)
                .build();
    }
}
