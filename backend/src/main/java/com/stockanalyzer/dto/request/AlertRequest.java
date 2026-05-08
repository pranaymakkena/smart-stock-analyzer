package com.stockanalyzer.dto.request;

import com.stockanalyzer.entity.enums.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AlertRequest {

    @NotBlank(message = "Stock symbol is required")
    private String symbol;

    @NotNull(message = "Alert type is required")
    private AlertType alertType;

    private BigDecimal targetPrice;

    private BigDecimal targetPercent;

    private String message;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getTargetPercent() { return targetPercent; }
    public void setTargetPercent(BigDecimal targetPercent) { this.targetPercent = targetPercent; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
