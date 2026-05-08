package com.stockanalyzer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private Long id;
    private String symbol;
    private String companyName;
    private String type;
    private Integer quantity;
    private BigDecimal pricePerShare;
    private BigDecimal totalAmount;
    private BigDecimal balanceAfter;
    private String notes;
    private LocalDateTime transactionDate;

    private TransactionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPricePerShare() { return pricePerShare; }
    public void setPricePerShare(BigDecimal pricePerShare) { this.pricePerShare = pricePerShare; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final TransactionResponse instance = new TransactionResponse();
        public Builder id(Long v) { instance.id = v; return this; }
        public Builder symbol(String v) { instance.symbol = v; return this; }
        public Builder companyName(String v) { instance.companyName = v; return this; }
        public Builder type(String v) { instance.type = v; return this; }
        public Builder quantity(Integer v) { instance.quantity = v; return this; }
        public Builder pricePerShare(BigDecimal v) { instance.pricePerShare = v; return this; }
        public Builder totalAmount(BigDecimal v) { instance.totalAmount = v; return this; }
        public Builder balanceAfter(BigDecimal v) { instance.balanceAfter = v; return this; }
        public Builder notes(String v) { instance.notes = v; return this; }
        public Builder transactionDate(LocalDateTime v) { instance.transactionDate = v; return this; }
        public TransactionResponse build() { return instance; }
    }
}
