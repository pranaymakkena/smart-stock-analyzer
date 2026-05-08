package com.stockanalyzer.dto.response;

import java.math.BigDecimal;

/**
 * DTO carrying prediction output from any strategy.
 */
public class PredictionResult {
    private String strategyName;
    private String trend;           // UPTREND, DOWNTREND, SIDEWAYS
    private BigDecimal predictedPrice;
    private BigDecimal confidence;  // 0-100
    private BigDecimal ma5;
    private BigDecimal ma20;
    private BigDecimal ma50;
    private BigDecimal rsi;
    private BigDecimal macd;
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerLower;
    private String recommendation;  // BUY, SELL, HOLD
    private String analysis;

    private PredictionResult() {}

    public String getStrategyName() { return strategyName; }
    public void setStrategyName(String strategyName) { this.strategyName = strategyName; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public BigDecimal getPredictedPrice() { return predictedPrice; }
    public void setPredictedPrice(BigDecimal predictedPrice) { this.predictedPrice = predictedPrice; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public BigDecimal getMa5() { return ma5; }
    public void setMa5(BigDecimal ma5) { this.ma5 = ma5; }

    public BigDecimal getMa20() { return ma20; }
    public void setMa20(BigDecimal ma20) { this.ma20 = ma20; }

    public BigDecimal getMa50() { return ma50; }
    public void setMa50(BigDecimal ma50) { this.ma50 = ma50; }

    public BigDecimal getRsi() { return rsi; }
    public void setRsi(BigDecimal rsi) { this.rsi = rsi; }

    public BigDecimal getMacd() { return macd; }
    public void setMacd(BigDecimal macd) { this.macd = macd; }

    public BigDecimal getBollingerUpper() { return bollingerUpper; }
    public void setBollingerUpper(BigDecimal bollingerUpper) { this.bollingerUpper = bollingerUpper; }

    public BigDecimal getBollingerLower() { return bollingerLower; }
    public void setBollingerLower(BigDecimal bollingerLower) { this.bollingerLower = bollingerLower; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PredictionResult instance = new PredictionResult();
        public Builder strategyName(String v) { instance.strategyName = v; return this; }
        public Builder trend(String v) { instance.trend = v; return this; }
        public Builder predictedPrice(BigDecimal v) { instance.predictedPrice = v; return this; }
        public Builder confidence(BigDecimal v) { instance.confidence = v; return this; }
        public Builder ma5(BigDecimal v) { instance.ma5 = v; return this; }
        public Builder ma20(BigDecimal v) { instance.ma20 = v; return this; }
        public Builder ma50(BigDecimal v) { instance.ma50 = v; return this; }
        public Builder rsi(BigDecimal v) { instance.rsi = v; return this; }
        public Builder macd(BigDecimal v) { instance.macd = v; return this; }
        public Builder bollingerUpper(BigDecimal v) { instance.bollingerUpper = v; return this; }
        public Builder bollingerLower(BigDecimal v) { instance.bollingerLower = v; return this; }
        public Builder recommendation(String v) { instance.recommendation = v; return this; }
        public Builder analysis(String v) { instance.analysis = v; return this; }
        public PredictionResult build() { return instance; }
    }
}
