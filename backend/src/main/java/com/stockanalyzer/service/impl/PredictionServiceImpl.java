package com.stockanalyzer.service.impl;

import com.stockanalyzer.dto.response.PredictionResult;
import com.stockanalyzer.dto.response.RiskAnalysisResponse;
import com.stockanalyzer.entity.*;
import com.stockanalyzer.repository.*;
import com.stockanalyzer.service.PredictionService;
import com.stockanalyzer.strategy.LinearRegressionPrediction;
import com.stockanalyzer.strategy.MovingAveragePrediction;
import com.stockanalyzer.strategy.PredictionStrategy;
import com.stockanalyzer.strategy.TrendAnalysisPrediction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionServiceImpl implements PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionServiceImpl.class);

    private final StockRepository stockRepository;
    private final StockHistoryRepository historyRepository;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final WatchlistRepository watchlistRepository;

    private final MovingAveragePrediction movingAveragePrediction;
    private final LinearRegressionPrediction linearRegressionPrediction;
    private final TrendAnalysisPrediction trendAnalysisPrediction;

    public PredictionServiceImpl(StockRepository stockRepository,
                                  StockHistoryRepository historyRepository,
                                  PredictionRepository predictionRepository,
                                  UserRepository userRepository,
                                  PortfolioRepository portfolioRepository,
                                  WatchlistRepository watchlistRepository,
                                  MovingAveragePrediction movingAveragePrediction,
                                  LinearRegressionPrediction linearRegressionPrediction,
                                  TrendAnalysisPrediction trendAnalysisPrediction) {
        this.stockRepository = stockRepository;
        this.historyRepository = historyRepository;
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.watchlistRepository = watchlistRepository;
        this.movingAveragePrediction = movingAveragePrediction;
        this.linearRegressionPrediction = linearRegressionPrediction;
        this.trendAnalysisPrediction = trendAnalysisPrediction;
    }

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    @Override
    public PredictionResult predictStock(String symbol, String strategy) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        List<StockHistory> history = historyRepository.findByStockOrderByDateAsc(stock);

        PredictionStrategy selectedStrategy = switch (strategy.toLowerCase()) {
            case "regression", "linear" -> linearRegressionPrediction;
            case "trend", "macd"        -> trendAnalysisPrediction;
            default                     -> movingAveragePrediction;
        };

        PredictionResult result = selectedStrategy.predict(history);
        savePrediction(stock, result);
        return result;
    }

    @Override
    public List<PredictionResult> predictAllStrategies(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        List<StockHistory> history = historyRepository.findByStockOrderByDateAsc(stock);

        List<PredictionStrategy> strategies = List.of(
            movingAveragePrediction,
            linearRegressionPrediction,
            trendAnalysisPrediction
        );

        return strategies.stream()
                .map(s -> {
                    PredictionResult r = s.predict(history);
                    savePrediction(stock, r);
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RiskAnalysisResponse analyzeRisk(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        List<StockHistory> history = historyRepository.findByStockOrderByDateAsc(stock);

        if (history.size() < 10) {
            return RiskAnalysisResponse.builder()
                    .symbol(symbol)
                    .riskScore(5)
                    .riskLevel("MEDIUM")
                    .riskSummary("Insufficient historical data for full risk analysis.")
                    .build();
        }

        // Calculate daily returns
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < history.size(); i++) {
            double prev = history.get(i - 1).getClose().doubleValue();
            double curr = history.get(i).getClose().doubleValue();
            if (prev > 0) returns.add((curr - prev) / prev);
        }

        double avgReturn = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = returns.stream()
                .mapToDouble(r -> Math.pow(r - avgReturn, 2))
                .average().orElse(0);
        double volatility = Math.sqrt(variance) * Math.sqrt(252); // annualized

        // Max drawdown
        double maxDrawdown = calculateMaxDrawdown(history);

        // Sharpe ratio (assuming risk-free rate = 2%)
        double riskFreeRate = 0.02;
        double annualReturn = avgReturn * 252;
        double sharpe = volatility > 0 ? (annualReturn - riskFreeRate) / volatility : 0;

        // Risk score 1-10
        int riskScore;
        String riskLevel;
        if (volatility < 0.15) {
            riskScore = 2; riskLevel = "LOW";
        } else if (volatility < 0.25) {
            riskScore = 4; riskLevel = "LOW-MEDIUM";
        } else if (volatility < 0.40) {
            riskScore = 6; riskLevel = "MEDIUM";
        } else if (volatility < 0.60) {
            riskScore = 8; riskLevel = "HIGH";
        } else {
            riskScore = 10; riskLevel = "VERY_HIGH";
        }

        String summary = String.format(
            "%s shows %.1f%% annualized volatility. Max drawdown: %.1f%%. " +
            "Sharpe ratio: %.2f. Risk level: %s.",
            symbol, volatility * 100, maxDrawdown * 100, sharpe, riskLevel
        );

        return RiskAnalysisResponse.builder()
                .symbol(symbol)
                .volatility(BigDecimal.valueOf(volatility).setScale(4, RoundingMode.HALF_UP))
                .sharpeRatio(BigDecimal.valueOf(sharpe).setScale(4, RoundingMode.HALF_UP))
                .maxDrawdown(BigDecimal.valueOf(maxDrawdown).setScale(4, RoundingMode.HALF_UP))
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .riskSummary(summary)
                .build();
    }

    @Override
    public Map<String, Object> getRecommendations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> recommendations = new LinkedHashMap<>();
        List<String> insights = new ArrayList<>();

        // Analyze watchlist
        List<Watchlist> watchlists = watchlistRepository.findByUser(user);
        List<String> watchlistSymbols = watchlists.stream()
                .flatMap(w -> w.getStocks().stream())
                .map(Stock::getSymbol)
                .distinct()
                .collect(Collectors.toList());

        // Analyze portfolio
        List<Portfolio> portfolios = portfolioRepository.findByUser(user);

        // Generate insights
        if (!watchlistSymbols.isEmpty()) {
            insights.add("You are watching " + watchlistSymbols.size() + " stocks. " +
                "Consider diversifying across sectors.");
        }

        if (portfolios.isEmpty()) {
            insights.add("Start your investment journey! Create a portfolio and buy your first stock.");
        } else {
            BigDecimal totalProfit = portfolios.stream()
                    .map(p -> p.getTotalProfit() != null ? p.getTotalProfit() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalProfit.compareTo(BigDecimal.ZERO) > 0) {
                insights.add("Your portfolio is up ₹" + totalProfit.setScale(2, RoundingMode.HALF_UP) +
                    ". Consider taking partial profits.");
            } else if (totalProfit.compareTo(BigDecimal.ZERO) < 0) {
                insights.add("Your portfolio is down. Consider averaging down on fundamentally strong stocks.");
            }
        }

        // Sector recommendations
        insights.add("Technology stocks are showing strong momentum this week.");
        insights.add("Banking sector is consolidating — watch for breakout opportunities.");
        insights.add("Consider adding defensive stocks (Pharma, FMCG) for portfolio stability.");

        recommendations.put("insights", insights);
        recommendations.put("watchlistSymbols", watchlistSymbols);
        recommendations.put("portfolioCount", portfolios.size());
        recommendations.put("generatedAt", java.time.LocalDateTime.now());

        return recommendations;
    }

    @Override
    public List<Map<String, Object>> getTechnicalIndicators(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        List<StockHistory> history = historyRepository.findByStockOrderByDateAsc(stock);

        List<Map<String, Object>> indicators = new ArrayList<>();

        // Run all strategies and collect indicators
        if (history.size() >= 5) {
            PredictionResult ma = movingAveragePrediction.predict(history);
            Map<String, Object> maIndicator = new LinkedHashMap<>();
            maIndicator.put("name", "Moving Averages");
            maIndicator.put("ma5", ma.getMa5());
            maIndicator.put("ma20", ma.getMa20());
            maIndicator.put("ma50", ma.getMa50());
            maIndicator.put("rsi", ma.getRsi());
            maIndicator.put("signal", ma.getRecommendation());
            indicators.add(maIndicator);
        }

        if (history.size() >= 26) {
            PredictionResult trend = trendAnalysisPrediction.predict(history);
            Map<String, Object> trendIndicator = new LinkedHashMap<>();
            trendIndicator.put("name", "MACD & Bollinger Bands");
            trendIndicator.put("macd", trend.getMacd());
            trendIndicator.put("bollingerUpper", trend.getBollingerUpper());
            trendIndicator.put("bollingerLower", trend.getBollingerLower());
            trendIndicator.put("signal", trend.getRecommendation());
            indicators.add(trendIndicator);
        }

        return indicators;
    }

    private void savePrediction(Stock stock, PredictionResult result) {
        Prediction prediction = new Prediction();
        prediction.setStock(stock);
        prediction.setStrategyUsed(result.getStrategyName());
        prediction.setTrend(result.getTrend());
        prediction.setPredictedPrice(result.getPredictedPrice());
        prediction.setConfidence(result.getConfidence());
        prediction.setMovingAverage5(result.getMa5());
        prediction.setMovingAverage20(result.getMa20());
        prediction.setMovingAverage50(result.getMa50());
        prediction.setRsi(result.getRsi());
        prediction.setMacd(result.getMacd());
        prediction.setBollingerUpper(result.getBollingerUpper());
        prediction.setBollingerLower(result.getBollingerLower());
        prediction.setRecommendation(result.getRecommendation());
        prediction.setAnalysis(result.getAnalysis());
        predictionRepository.save(prediction);
    }

    private double calculateMaxDrawdown(List<StockHistory> history) {
        double peak = Double.MIN_VALUE;
        double maxDrawdown = 0;
        for (StockHistory h : history) {
            double price = h.getClose().doubleValue();
            if (price > peak) peak = price;
            double drawdown = (peak - price) / peak;
            if (drawdown > maxDrawdown) maxDrawdown = drawdown;
        }
        return maxDrawdown;
    }
}
