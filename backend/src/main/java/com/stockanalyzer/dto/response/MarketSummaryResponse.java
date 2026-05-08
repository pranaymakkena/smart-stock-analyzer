package com.stockanalyzer.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class MarketSummaryResponse {
    private List<StockResponse> topGainers;
    private List<StockResponse> topLosers;
    private List<StockResponse> mostActive;
    private Map<String, BigDecimal> sectorPerformance;
    private long totalStocksTracked;
    private long gainersCount;
    private long losersCount;

    private MarketSummaryResponse() {}

    public List<StockResponse> getTopGainers() { return topGainers; }
    public void setTopGainers(List<StockResponse> topGainers) { this.topGainers = topGainers; }

    public List<StockResponse> getTopLosers() { return topLosers; }
    public void setTopLosers(List<StockResponse> topLosers) { this.topLosers = topLosers; }

    public List<StockResponse> getMostActive() { return mostActive; }
    public void setMostActive(List<StockResponse> mostActive) { this.mostActive = mostActive; }

    public Map<String, BigDecimal> getSectorPerformance() { return sectorPerformance; }
    public void setSectorPerformance(Map<String, BigDecimal> sectorPerformance) { this.sectorPerformance = sectorPerformance; }

    public long getTotalStocksTracked() { return totalStocksTracked; }
    public void setTotalStocksTracked(long totalStocksTracked) { this.totalStocksTracked = totalStocksTracked; }

    public long getGainersCount() { return gainersCount; }
    public void setGainersCount(long gainersCount) { this.gainersCount = gainersCount; }

    public long getLosersCount() { return losersCount; }
    public void setLosersCount(long losersCount) { this.losersCount = losersCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MarketSummaryResponse instance = new MarketSummaryResponse();
        public Builder topGainers(List<StockResponse> v) { instance.topGainers = v; return this; }
        public Builder topLosers(List<StockResponse> v) { instance.topLosers = v; return this; }
        public Builder mostActive(List<StockResponse> v) { instance.mostActive = v; return this; }
        public Builder sectorPerformance(Map<String, BigDecimal> v) { instance.sectorPerformance = v; return this; }
        public Builder totalStocksTracked(long v) { instance.totalStocksTracked = v; return this; }
        public Builder gainersCount(long v) { instance.gainersCount = v; return this; }
        public Builder losersCount(long v) { instance.losersCount = v; return this; }
        public MarketSummaryResponse build() { return instance; }
    }
}
