package com.sentinelx.ingestion.controller;

import com.sentinelx.ingestion.model.FraudAlertEntity;
import com.sentinelx.ingestion.rule.FraudRule;
import com.sentinelx.ingestion.service.FraudAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Fraud Detection API", description = "Endpoints for fraud rule catalog and SOC fraud alert triage management")
@CrossOrigin(origins = "*")
public class FraudRuleController {

    private final List<FraudRule> registeredRules;
    private final FraudAlertService fraudAlertService;

    @GetMapping("/rules")
    @Operation(summary = "List Active Fraud Rules", description = "Returns active rule catalog with weights and detection criteria")
    public ResponseEntity<List<Map<String, String>>> getRules() {
        List<Map<String, String>> ruleCatalog = registeredRules.stream()
                .map(r -> Map.of(
                        "ruleCode", r.getRuleCode(),
                        "ruleName", r.getRuleName()
                ))
                .toList();
        return ResponseEntity.ok(ruleCatalog);
    }

    @GetMapping("/alerts/fraud")
    @Operation(summary = "Get Fraud Alerts", description = "Returns paginated fraud alerts with optional status filtering")
    public ResponseEntity<Page<FraudAlertEntity>> getFraudAlerts(
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(fraudAlertService.getAlerts(status, page, size));
    }

    @PatchMapping("/alerts/{alertId}/status")
    @Operation(summary = "Update Alert Status", description = "Updates alert status (NEW, ACKNOWLEDGED, INVESTIGATING, RESOLVED, FALSE_POSITIVE) with transition validation")
    public ResponseEntity<FraudAlertEntity> updateAlertStatus(
            @PathVariable String alertId,
            @RequestBody Map<String, String> payload
    ) {
        String newStatus = payload.get("status");
        if (newStatus == null) {
            throw new IllegalArgumentException("Status field is mandatory in request body");
        }
        FraudAlertEntity updated = fraudAlertService.updateAlertStatus(alertId, newStatus);
        return ResponseEntity.ok(updated);
    }
}
