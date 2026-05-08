package com.stockanalyzer.entity;

import com.stockanalyzer.entity.enums.Sector;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stock entity — encapsulates all stock market data.
 */
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String symbol;

    @Column(nullable = false)
    private String companyName;

    @Enumerated(EnumType.STRING)
    private Sector sector;

    @Column(precision = 15, scale = 4)
    private BigDecimal currentPrice;

    @Column(precision = 15, scale = 4)
    private BigDecimal openPrice;

    @Column(precision = 15, scale = 4)
    private BigDecimal dayHigh;

    @Column(precision = 15, scale = 4)
    private BigDecimal dayLow;

    @Column(precision = 15, scale = 4)
    private BigDecimal previousClose;

    @Column(precision = 10, scale = 2)
    private BigDecimal changePercent;

    @Column(precision = 15, scale = 4)
    private BigDecimal change;

    private Long volume;

    private Long avgVolume;

    @Column(precision = 20, scale = 2)
    private BigDecimal marketCap;

    @Column(precision = 10, scale = 2)
    private BigDecimal peRatio;

    @Column(precision = 10, scale = 4)
    private BigDecimal eps;

    @Column(precision = 10, scale = 4)
    private BigDecimal dividendYield;

    @Column(precision = 15, scale = 4)
    private BigDecimal fiftyTwoWeekHigh;

    @Column(precision = 15, scale = 4)
    private BigDecimal fiftyTwoWeekLow;

    private String exchange;

    private String currency = "USD";

    private String logoUrl;

    private String description;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    public Stock() {}

    public Stock(String symbol, String companyName, Sector sector) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }

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

    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }

    public BigDecimal getChange() { return change; }
    public void setChange(BigDecimal change) { this.change = change; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public Long getAvgVolume() { return avgVolume; }
    public void setAvgVolume(Long avgVolume) { this.avgVolume = avgVolume; }

    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }

    public BigDecimal getPeRatio() { return peRatio; }
    public void setPeRatio(BigDecimal peRatio) { this.peRatio = peRatio; }

    public BigDecimal getEps() { return eps; }
    public void setEps(BigDecimal eps) { this.eps = eps; }

    public BigDecimal getDividendYield() { return dividendYield; }
    public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }

    public BigDecimal getFiftyTwoWeekHigh() { return fiftyTwoWeekHigh; }
    public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) { this.fiftyTwoWeekHigh = fiftyTwoWeekHigh; }

    public BigDecimal getFiftyTwoWeekLow() { return fiftyTwoWeekLow; }
    public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) { this.fiftyTwoWeekLow = fiftyTwoWeekLow; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
