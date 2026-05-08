package com.stockanalyzer.dto.response;

import java.math.BigDecimal;

public class PortfolioItemResponse {
    private Long id;
    private String symbol;
    private String companyName;
    private String sector;
    private Integer quantity;
    private BigDecimal avgBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal investedAmount;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPercent;

    private PortfolioItemResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getAvgBuyPrice() { return avgBuyPrice; }
    public void setAvgBuyPrice(BigDecimal avgBuyPrice) { this.avgBuyPrice = avgBuyPrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public BigDecimal getInvestedAmount() { return investedAmount; }
    public void setInvestedAmount(BigDecimal investedAmount) { this.investedAmount = investedAmount; }

    public BigDecimal getProfitLoss() { return profitLoss; }
    public void setProfitLoss(BigDecimal profitLoss) { this.profitLoss = profitLoss; }

    public BigDecimal getProfitLossPercent() { return profitLossPercent; }
    public void setProfitLossPercent(BigDecimal profitLossPercent) { this.profitLossPercent = profitLossPercent; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PortfolioItemResponse instance = new PortfolioItemResponse();
        public Builder id(Long v) { instance.id = v; return this; }
        public Builder symbol(String v) { instance.symbol = v; return this; }
        public Builder companyName(String v) { instance.companyName = v; return this; }
        public Builder sector(String v) { instance.sector = v; return this; }
        public Builder quantity(Integer v) { instance.quantity = v; return this; }
        public Builder avgBuyPrice(BigDecimal v) { instance.avgBuyPrice = v; return this; }
        public Builder currentPrice(BigDecimal v) { instance.currentPrice = v; return this; }
        public Builder currentValue(BigDecimal v) { instance.currentValue = v; return this; }
        public Builder investedAmount(BigDecimal v) { instance.investedAmount = v; return this; }
        public Builder profitLoss(BigDecimal v) { instance.profitLoss = v; return this; }
        public Builder profitLossPercent(BigDecimal v) { instance.profitLossPercent = v; return this; }
        public PortfolioItemResponse build() { return instance; }
    }
}
