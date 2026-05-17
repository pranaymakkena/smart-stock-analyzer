package com.stockanalyzer.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockanalyzer.dto.request.AlertRequest;
import com.stockanalyzer.entity.Alert;
import com.stockanalyzer.entity.Stock;
import com.stockanalyzer.entity.User;
import com.stockanalyzer.repository.AlertRepository;
import com.stockanalyzer.repository.StockRepository;
import com.stockanalyzer.repository.UserRepository;
import com.stockanalyzer.service.AlertService;

@Service
public class AlertServiceImpl implements AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertServiceImpl.class);

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    public AlertServiceImpl(AlertRepository alertRepository, UserRepository userRepository,
                             StockRepository stockRepository) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public Alert createAlert(AlertRequest request, String email) {
        User user = getUser(email);
        Stock stock = stockRepository.findBySymbol(request.getSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + request.getSymbol()));

        Alert alert = new Alert();
        alert.setUser(user);
        alert.setStock(stock);
        alert.setAlertType(request.getAlertType());
        alert.setTargetPrice(request.getTargetPrice());
        alert.setTargetPercent(request.getTargetPercent());
        alert.setMessage(request.getMessage());
        return alertRepository.save(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alert> getUserAlerts(String email) {
        User user = getUser(email);
        return alertRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional
    public void deleteAlert(Long alertId, String email) {
        User user = getUser(email);
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        alertRepository.delete(alert);
    }

    @Override
    @Transactional
    public void toggleAlert(Long alertId, String email) {
        User user = getUser(email);
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        alert.setActive(!alert.isActive());
        alertRepository.save(alert);
    }

    @Override
    @Scheduled(fixedDelay = 60000) // Check every minute
    @Transactional
    public void checkAndTriggerAlerts() {
        List<Alert> activeAlerts = alertRepository.findAllActiveUntriggered();
        for (Alert alert : activeAlerts) {
            try {
                checkAlert(alert);
            } catch (Exception e) {
                log.error("Error checking alert {}: {}", alert.getId(), e.getMessage());
            }
        }
    }

    private void checkAlert(Alert alert) {
        Stock stock = alert.getStock();
        BigDecimal currentPrice = stock.getCurrentPrice();
        if (currentPrice == null) return;

        boolean triggered = false;
        String triggerMessage = "";

        switch (alert.getAlertType()) {
            case PRICE_ABOVE:
                if (alert.getTargetPrice() != null && currentPrice.compareTo(alert.getTargetPrice()) >= 0) {
                    triggered = true;
                    triggerMessage = stock.getSymbol() + " crossed above ₹" + alert.getTargetPrice();
                }
                break;
            case PRICE_BELOW:
                if (alert.getTargetPrice() != null && currentPrice.compareTo(alert.getTargetPrice()) <= 0) {
                    triggered = true;
                    triggerMessage = stock.getSymbol() + " dropped below ₹" + alert.getTargetPrice();
                }
                break;
            case VOLUME_SPIKE:
                if (stock.getVolume() != null && stock.getAvgVolume() != null
                        && stock.getVolume() > stock.getAvgVolume() * 2) {
                    triggered = true;
                    triggerMessage = stock.getSymbol() + " volume spike detected!";
                }
                break;
            case MARKET_DROP:
                if (stock.getChangePercent() != null
                        && stock.getChangePercent().compareTo(BigDecimal.valueOf(-5)) <= 0) {
                    triggered = true;
                    triggerMessage = stock.getSymbol() + " dropped " + stock.getChangePercent() + "% today!";
                }
                break;
            case PERCENT_CHANGE:
                if (alert.getTargetPercent() != null && stock.getChangePercent() != null
                        && Math.abs(stock.getChangePercent().doubleValue()) >= Math.abs(alert.getTargetPercent().doubleValue())) {
                    triggered = true;
                    triggerMessage = stock.getSymbol() + " moved " + stock.getChangePercent() + "%";
                }
                break;
        }

        if (triggered) {
            alert.setTriggered(true);
            alert.setActive(false);
            alert.setTriggeredAt(LocalDateTime.now());
            if (alert.getMessage() == null) alert.setMessage(triggerMessage);
            alertRepository.save(alert);
            log.info("Alert triggered: {}", triggerMessage);
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
