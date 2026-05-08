package com.stockanalyzer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalyzer.dto.request.AlertRequest;
import com.stockanalyzer.dto.response.ApiResponse;
import com.stockanalyzer.entity.Alert;
import com.stockanalyzer.service.AlertService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Alert>>> getAlerts(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            alertService.getUserAlerts(userDetails.getUsername())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Alert>> createAlert(
            @Valid @RequestBody AlertRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Alert created",
            alertService.createAlert(request, userDetails.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAlert(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        alertService.deleteAlert(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Alert deleted", null));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<String>> toggleAlert(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        alertService.toggleAlert(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Alert toggled", null));
    }
}
