package com.stockanalyzer.dto.response;

import java.math.BigDecimal;

public class LeaderboardEntry {
    private int rank;
    private Long userId;
    private String fullName;
    private BigDecimal totalProfit;
    private BigDecimal profitPercent;
    private BigDecimal portfolioValue;
    private long totalTrades;

    private LeaderboardEntry() {}

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public BigDecimal getProfitPercent() { return profitPercent; }
    public void setProfitPercent(BigDecimal profitPercent) { this.profitPercent = profitPercent; }

    public BigDecimal getPortfolioValue() { return portfolioValue; }
    public void setPortfolioValue(BigDecimal portfolioValue) { this.portfolioValue = portfolioValue; }

    public long getTotalTrades() { return totalTrades; }
    public void setTotalTrades(long totalTrades) { this.totalTrades = totalTrades; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final LeaderboardEntry instance = new LeaderboardEntry();
        public Builder rank(int v) { instance.rank = v; return this; }
        public Builder userId(Long v) { instance.userId = v; return this; }
        public Builder fullName(String v) { instance.fullName = v; return this; }
        public Builder totalProfit(BigDecimal v) { instance.totalProfit = v; return this; }
        public Builder profitPercent(BigDecimal v) { instance.profitPercent = v; return this; }
        public Builder portfolioValue(BigDecimal v) { instance.portfolioValue = v; return this; }
        public Builder totalTrades(long v) { instance.totalTrades = v; return this; }
        public LeaderboardEntry build() { return instance; }
    }
}
