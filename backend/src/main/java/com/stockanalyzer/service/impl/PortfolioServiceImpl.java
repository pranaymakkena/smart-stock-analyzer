package com.stockanalyzer.service.impl;

import com.stockanalyzer.dto.request.TradeRequest;
import com.stockanalyzer.dto.response.PortfolioItemResponse;
import com.stockanalyzer.dto.response.PortfolioResponse;
import com.stockanalyzer.dto.response.TransactionResponse;
import com.stockanalyzer.entity.*;
import com.stockanalyzer.entity.enums.TransactionType;
import com.stockanalyzer.repository.*;
import com.stockanalyzer.service.PortfolioService;
import com.stockanalyzer.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
                                PortfolioItemRepository portfolioItemRepository,
                                TransactionRepository transactionRepository,
                                UserRepository userRepository,
                                StockRepository stockRepository,
                                StockService stockService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    @Override
    public List<PortfolioResponse> getUserPortfolios(String email) {
        User user = getUser(email);
        return portfolioRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PortfolioResponse getPortfolioById(Long id, String email) {
        User user = getUser(email);
        Portfolio portfolio = portfolioRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        return mapToResponse(portfolio);
    }

    @Override
    @Transactional
    public PortfolioResponse createPortfolio(String name, String email) {
        User user = getUser(email);
        if (portfolioRepository.existsByUserAndName(user, name)) {
            throw new RuntimeException("Portfolio with name '" + name + "' already exists");
        }
        Portfolio portfolio = new Portfolio(user, name);
        return mapToResponse(portfolioRepository.save(portfolio));
    }

    @Override
    @Transactional
    public PortfolioResponse buyStock(TradeRequest request, String email) {
        User user = getUser(email);
        Stock stock = stockRepository.findBySymbol(request.getSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + request.getSymbol()));

        BigDecimal price = stock.getCurrentPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Stock price not available");
        }

        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(request.getQuantity()));

        if (user.getVirtualBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient balance. Required: " + totalCost +
                    ", Available: " + user.getVirtualBalance());
        }

        // Deduct balance
        user.setVirtualBalance(user.getVirtualBalance().subtract(totalCost));
        user.setTotalInvested(user.getTotalInvested().add(totalCost));
        userRepository.save(user);

        // Get or create default portfolio
        Portfolio portfolio = getOrCreateDefaultPortfolio(user, request.getPortfolioId());

        // Update portfolio item
        PortfolioItem item = portfolioItemRepository.findByPortfolioAndStock(portfolio, stock)
                .orElseGet(() -> {
                    PortfolioItem newItem = new PortfolioItem();
                    newItem.setPortfolio(portfolio);
                    newItem.setStock(stock);
                    newItem.setQuantity(0);
                    newItem.setAvgBuyPrice(BigDecimal.ZERO);
                    return newItem;
                });

        // Calculate new average buy price
        BigDecimal existingValue = item.getAvgBuyPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        int newQuantity = item.getQuantity() + request.getQuantity();
        BigDecimal newAvgPrice = existingValue.add(totalCost)
                .divide(BigDecimal.valueOf(newQuantity), 4, RoundingMode.HALF_UP);

        item.setQuantity(newQuantity);
        item.setAvgBuyPrice(newAvgPrice);
        item.setInvestedAmount(newAvgPrice.multiply(BigDecimal.valueOf(newQuantity)));
        item.setCurrentValue(price.multiply(BigDecimal.valueOf(newQuantity)));
        item.setProfitLoss(item.getCurrentValue().subtract(item.getInvestedAmount()));
        portfolioItemRepository.save(item);

        // Record transaction
        Transaction tx = new Transaction(user, stock, TransactionType.BUY,
                request.getQuantity(), price, totalCost);
        tx.setBalanceAfter(user.getVirtualBalance());
        transactionRepository.save(tx);

        // Refresh portfolio totals
        refreshPortfolioTotals(portfolio);
        return mapToResponse(portfolio);
    }

    @Override
    @Transactional
    public PortfolioResponse sellStock(TradeRequest request, String email) {
        User user = getUser(email);
        Stock stock = stockRepository.findBySymbol(request.getSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + request.getSymbol()));

        Portfolio portfolio = getOrCreateDefaultPortfolio(user, request.getPortfolioId());

        PortfolioItem item = portfolioItemRepository.findByPortfolioAndStock(portfolio, stock)
                .orElseThrow(() -> new RuntimeException("You don't own " + request.getSymbol()));

        if (item.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient shares. You own: " + item.getQuantity());
        }

        BigDecimal price = stock.getCurrentPrice();
        BigDecimal totalRevenue = price.multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal costBasis = item.getAvgBuyPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal profit = totalRevenue.subtract(costBasis);

        // Update balance
        user.setVirtualBalance(user.getVirtualBalance().add(totalRevenue));
        user.setTotalProfit(user.getTotalProfit().add(profit));
        userRepository.save(user);

        // Update portfolio item
        int newQuantity = item.getQuantity() - request.getQuantity();
        if (newQuantity == 0) {
            portfolioItemRepository.delete(item);
        } else {
            item.setQuantity(newQuantity);
            item.setInvestedAmount(item.getAvgBuyPrice().multiply(BigDecimal.valueOf(newQuantity)));
            item.setCurrentValue(price.multiply(BigDecimal.valueOf(newQuantity)));
            item.setProfitLoss(item.getCurrentValue().subtract(item.getInvestedAmount()));
            portfolioItemRepository.save(item);
        }

        // Record transaction
        Transaction tx = new Transaction(user, stock, TransactionType.SELL,
                request.getQuantity(), price, totalRevenue);
        tx.setBalanceAfter(user.getVirtualBalance());
        transactionRepository.save(tx);

        refreshPortfolioTotals(portfolio);
        return mapToResponse(portfolio);
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(String email) {
        User user = getUser(email);
        return transactionRepository.findByUserOrderByTransactionDateDesc(user).stream()
                .map(this::mapTransactionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void refreshPortfolioValues(String email) {
        User user = getUser(email);
        portfolioRepository.findByUser(user).forEach(this::refreshPortfolioTotals);
    }

    @Override
    @Transactional
    public void deletePortfolio(Long id, String email) {
        User user = getUser(email);
        Portfolio portfolio = portfolioRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        portfolioRepository.delete(portfolio);
    }

    private void refreshPortfolioTotals(Portfolio portfolio) {
        List<PortfolioItem> items = portfolioItemRepository.findByPortfolio(portfolio);
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;

        for (PortfolioItem item : items) {
            BigDecimal currentPrice = item.getStock().getCurrentPrice();
            if (currentPrice != null) {
                BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal invested = item.getAvgBuyPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setCurrentValue(currentValue);
                item.setInvestedAmount(invested);
                item.setProfitLoss(currentValue.subtract(invested));
                if (invested.compareTo(BigDecimal.ZERO) > 0) {
                    item.setProfitLossPercent(
                        item.getProfitLoss().divide(invested, 4, RoundingMode.HALF_UP)
                                            .multiply(BigDecimal.valueOf(100))
                    );
                }
                portfolioItemRepository.save(item);
                totalValue = totalValue.add(currentValue);
                totalInvested = totalInvested.add(invested);
            }
        }

        portfolio.setTotalValue(totalValue);
        portfolio.setTotalInvested(totalInvested);
        portfolio.setTotalProfit(totalValue.subtract(totalInvested));
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            portfolio.setProfitPercent(
                portfolio.getTotalProfit().divide(totalInvested, 4, RoundingMode.HALF_UP)
                                         .multiply(BigDecimal.valueOf(100))
            );
        }
        portfolioRepository.save(portfolio);
    }

    private Portfolio getOrCreateDefaultPortfolio(User user, Long portfolioId) {
        if (portfolioId != null) {
            return portfolioRepository.findByIdAndUser(portfolioId, user)
                    .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        }
        List<Portfolio> portfolios = portfolioRepository.findByUser(user);
        if (portfolios.isEmpty()) {
            Portfolio p = new Portfolio(user, "My Portfolio");
            return portfolioRepository.save(p);
        }
        return portfolios.get(0);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private PortfolioResponse mapToResponse(Portfolio portfolio) {
        List<PortfolioItem> items = portfolioItemRepository.findByPortfolio(portfolio);
        List<PortfolioItemResponse> itemResponses = items.stream()
                .map(item -> PortfolioItemResponse.builder()
                        .id(item.getId())
                        .symbol(item.getStock().getSymbol())
                        .companyName(item.getStock().getCompanyName())
                        .sector(item.getStock().getSector() != null ? item.getStock().getSector().name() : null)
                        .quantity(item.getQuantity())
                        .avgBuyPrice(item.getAvgBuyPrice())
                        .currentPrice(item.getStock().getCurrentPrice())
                        .currentValue(item.getCurrentValue())
                        .investedAmount(item.getInvestedAmount())
                        .profitLoss(item.getProfitLoss())
                        .profitLossPercent(item.getProfitLossPercent())
                        .build())
                .collect(Collectors.toList());

        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .totalValue(portfolio.getTotalValue())
                .totalInvested(portfolio.getTotalInvested())
                .totalProfit(portfolio.getTotalProfit())
                .profitPercent(portfolio.getProfitPercent())
                .items(itemResponses)
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }

    private TransactionResponse mapTransactionToResponse(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .symbol(tx.getStock().getSymbol())
                .companyName(tx.getStock().getCompanyName())
                .type(tx.getType().name())
                .quantity(tx.getQuantity())
                .pricePerShare(tx.getPricePerShare())
                .totalAmount(tx.getTotalAmount())
                .balanceAfter(tx.getBalanceAfter())
                .notes(tx.getNotes())
                .transactionDate(tx.getTransactionDate())
                .build();
    }
}
