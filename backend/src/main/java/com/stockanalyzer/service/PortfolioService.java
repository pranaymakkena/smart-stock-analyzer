package com.stockanalyzer.service;

import com.stockanalyzer.dto.request.TradeRequest;
import com.stockanalyzer.dto.response.PortfolioResponse;
import com.stockanalyzer.dto.response.TransactionResponse;

import java.util.List;

/**
 * Portfolio service interface — demonstrates ABSTRACTION.
 */
public interface PortfolioService {
    List<PortfolioResponse> getUserPortfolios(String email);
    PortfolioResponse getPortfolioById(Long id, String email);
    PortfolioResponse createPortfolio(String name, String email);
    PortfolioResponse buyStock(TradeRequest request, String email);
    PortfolioResponse sellStock(TradeRequest request, String email);
    List<TransactionResponse> getTransactionHistory(String email);
    void refreshPortfolioValues(String email);
    void deletePortfolio(Long id, String email);
}
