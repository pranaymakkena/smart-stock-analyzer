package com.stockanalyzer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Prediction entity — stores AI-like trend predictions.
 */
@Entity
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private String strategyUsed;

    @Column(nullable = false)
    private String trend; // UPTREND, DOWNTREND, SIDEWAYS

    @Column(precision = 15, scale = 4)
    private BigDecimal predictedPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal confidence; // 0-100%

    @Column(precision = 10, scale = 4)
    private BigDecimal movingAverage5;

    @Column(precision = 10, scale = 4)
    private BigDecimal movingAverage20;

    @Column(precision = 10, scale = 4)
    private BigDecimal movingAverage50;

    @Column(precision = 10, scale = 4)
    private BigDecimal rsi;

    @Column(precision = 10, scale = 4)
    private BigDecimal macd;

    @Column(precision = 10, scale = 4)
    private BigDecimal bollingerUpper;

    @Column(precision = 10, scale = 4)
    private BigDecimal bollingerLower;

    private String recommendation; // BUY, SELL, HOLD

    private String analysis;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Prediction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public String getStrategyUsed() { return strategyUsed; }
    public void setStrategyUsed(String strategyUsed) { this.strategyUsed = strategyUsed; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public BigDecimal getPredictedPrice() { return predictedPrice; }
    public void setPredictedPrice(BigDecimal predictedPrice) { this.predictedPrice = predictedPrice; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public BigDecimal getMovingAverage5() { return movingAverage5; }
    public void setMovingAverage5(BigDecimal movingAverage5) { this.movingAverage5 = movingAverage5; }

    public BigDecimal getMovingAverage20() { return movingAverage20; }
    public void setMovingAverage20(BigDecimal movingAverage20) { this.movingAverage20 = movingAverage20; }

    public BigDecimal getMovingAverage50() { return movingAverage50; }
    public void setMovingAverage50(BigDecimal movingAverage50) { this.movingAverage50 = movingAverage50; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
