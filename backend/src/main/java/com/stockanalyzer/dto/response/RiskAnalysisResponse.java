package com.stockanalyzer.dto.response;

import java.math.BigDecimal;

public class RiskAnalysisResponse {
    private String symbol;
    private BigDecimal volatility;         // standard deviation of returns
    private BigDecimal beta;               // relative to market
    private BigDecimal sharpeRatio;
    private BigDecimal maxDrawdown;
    private BigDecimal profitLossRatio;
    private int riskScore;                 // 1-10
    private String riskLevel;             // LOW, MEDIUM, HIGH, VERY_HIGH
    private String riskSummary;

    private RiskAnalysisResponse() {}

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getVolatility() { return volatility; }
    public void setVolatility(BigDecimal volatility) { this.volatility = volatility; }

    public BigDecimal getBeta() { return beta; }
    public void setBeta(BigDecimal beta) { this.beta = beta; }

    public BigDecimal getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(BigDecimal sharpeRatio) { this.sharpeRatio = sharpeRatio; }

    public BigDecimal getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(BigDecimal maxDrawdown) { this.maxDrawdown = maxDrawdown; }

    public BigDecimal getProfitLossRatio() { return profitLossRatio; }
    public void setProfitLossRatio(BigDecimal profitLossRatio) { this.profitLossRatio = profitLossRatio; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRiskSummary() { return riskSummary; }
    public void setRiskSummary(String riskSummary) { this.riskSummary = riskSummary; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final RiskAnalysisResponse instance = new RiskAnalysisResponse();
        public Builder symbol(String v) { instance.symbol = v; return this; }
        public Builder volatility(BigDecimal v) { instance.volatility = v; return this; }
        public Builder beta(BigDecimal v) { instance.beta = v; return this; }
        public Builder sharpeRatio(BigDecimal v) { instance.sharpeRatio = v; return this; }
        public Builder maxDrawdown(BigDecimal v) { instance.maxDrawdown = v; return this; }
        public Builder profitLossRatio(BigDecimal v) { instance.profitLossRatio = v; return this; }
        public Builder riskScore(int v) { instance.riskScore = v; return this; }
        public Builder riskLevel(String v) { instance.riskLevel = v; return this; }
        public Builder riskSummary(String v) { instance.riskSummary = v; return this; }
        public RiskAnalysisResponse build() { return instance; }
    }
}
