package com.stockanalyzer.service;

import com.stockanalyzer.dto.request.AlertRequest;
import com.stockanalyzer.entity.Alert;

import java.util.List;

public interface AlertService {
    Alert createAlert(AlertRequest request, String email);
    List<Alert> getUserAlerts(String email);
    void deleteAlert(Long alertId, String email);
    void toggleAlert(Long alertId, String email);
    void checkAndTriggerAlerts();
}
