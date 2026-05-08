package com.stockanalyzer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal totalValue;
    private BigDecimal totalInvested;
    private BigDecimal totalProfit;
    private BigDecimal profitPercent;
    private List<PortfolioItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PortfolioResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public BigDecimal getTotalInvested() { return totalInvested; }
    public void setTotalInvested(BigDecimal totalInvested) { this.totalInvested = totalInvested; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public BigDecimal getProfitPercent() { return profitPercent; }
    public void setProfitPercent(BigDecimal profitPercent) { this.profitPercent = profitPercent; }

    public List<PortfolioItemResponse> getItems() { return items; }
    public void setItems(List<PortfolioItemResponse> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PortfolioResponse instance = new PortfolioResponse();
        public Builder id(Long v) { instance.id = v; return this; }
        public Builder name(String v) { instance.name = v; return this; }
        public Builder description(String v) { instance.description = v; return this; }
        public Builder totalValue(BigDecimal v) { instance.totalValue = v; return this; }
        public Builder totalInvested(BigDecimal v) { instance.totalInvested = v; return this; }
        public Builder totalProfit(BigDecimal v) { instance.totalProfit = v; return this; }
        public Builder profitPercent(BigDecimal v) { instance.profitPercent = v; return this; }
        public Builder items(List<PortfolioItemResponse> v) { instance.items = v; return this; }
        public Builder createdAt(LocalDateTime v) { instance.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { instance.updatedAt = v; return this; }
        public PortfolioResponse build() { return instance; }
    }
}
