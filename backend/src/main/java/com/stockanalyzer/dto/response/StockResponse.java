package com.stockanalyzer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockResponse {
    private Long id;
    private String symbol;
    private String companyName;
    private String sector;
    private BigDecimal currentPrice;
    private BigDecimal openPrice;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
    private BigDecimal previousClose;
    private BigDecimal change;
    private BigDecimal changePercent;
    private Long volume;
    private Long avgVolume;
    private BigDecimal marketCap;
    private BigDecimal peRatio;
    private BigDecimal fiftyTwoWeekHigh;
    private BigDecimal fiftyTwoWeekLow;
    private String exchange;
    private String currency;
    private LocalDateTime lastUpdated;

    private StockResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getDayHigh() { return dayHigh; }
    public void setDayHigh(BigDecimal dayHigh) { this.dayHigh = dayHigh; }

    public BigDecimal getDayLow() { return dayLow; }
    public void setDayLow(BigDecimal dayLow) { this.dayLow = dayLow; }

    public BigDecimal getPreviousClose() { return previousClose; }
    public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }

    public BigDecimal getChange() { return change; }
    public void setChange(BigDecimal change) { this.change = change; }

    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public Long getAvgVolume() { return avgVolume; }
    public void setAvgVolume(Long avgVolume) { this.avgVolume = avgVolume; }

    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }

    public BigDecimal getPeRatio() { return peRatio; }
    public void setPeRatio(BigDecimal peRatio) { this.peRatio = peRatio; }

    public BigDecimal getFiftyTwoWeekHigh() { return fiftyTwoWeekHigh; }
    public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) { this.fiftyTwoWeekHigh = fiftyTwoWeekHigh; }

    public BigDecimal getFiftyTwoWeekLow() { return fiftyTwoWeekLow; }
    public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) { this.fiftyTwoWeekLow = fiftyTwoWeekLow; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StockResponse instance = new StockResponse();
        public Builder id(Long v) { instance.id = v; return this; }
        public Builder symbol(String v) { instance.symbol = v; return this; }
        public Builder companyName(String v) { instance.companyName = v; return this; }
        public Builder sector(String v) { instance.sector = v; return this; }
        public Builder currentPrice(BigDecimal v) { instance.currentPrice = v; return this; }
        public Builder openPrice(BigDecimal v) { instance.openPrice = v; return this; }
        public Builder dayHigh(BigDecimal v) { instance.dayHigh = v; return this; }
        public Builder dayLow(BigDecimal v) { instance.dayLow = v; return this; }
        public Builder previousClose(BigDecimal v) { instance.previousClose = v; return this; }
        public Builder change(BigDecimal v) { instance.change = v; return this; }
        public Builder changePercent(BigDecimal v) { instance.changePercent = v; return this; }
        public Builder volume(Long v) { instance.volume = v; return this; }
        public Builder avgVolume(Long v) { instance.avgVolume = v; return this; }
        public Builder marketCap(BigDecimal v) { instance.marketCap = v; return this; }
        public Builder peRatio(BigDecimal v) { instance.peRatio = v; return this; }
        public Builder fiftyTwoWeekHigh(BigDecimal v) { instance.fiftyTwoWeekHigh = v; return this; }
        public Builder fiftyTwoWeekLow(BigDecimal v) { instance.fiftyTwoWeekLow = v; return this; }
        public Builder exchange(String v) { instance.exchange = v; return this; }
        public Builder currency(String v) { instance.currency = v; return this; }
        public Builder lastUpdated(LocalDateTime v) { instance.lastUpdated = v; return this; }
        public StockResponse build() { return instance; }
    }
}
