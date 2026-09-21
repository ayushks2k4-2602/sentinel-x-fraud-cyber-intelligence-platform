package com.sentinelx.ingestion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import com.sentinelx.ingestion.model.FraudAlertEntity;
import com.sentinelx.ingestion.repository.FraudAlertRepository;
import com.sentinelx.ingestion.rule.RiskEvaluation;
import com.sentinelx.ingestion.rule.RuleContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final RuleEngineService ruleEngineService;
    private final RedisVelocityService redisVelocityService;
    private final FraudAlertRepository fraudAlertRepository;
    private final KafkaProducerService kafkaProducerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Evaluates incoming financial event and generates idempotent fraud alert if risk threshold is met.
     */
    @Transactional
    public RiskEvaluation evaluateAndAlert(EventEnvelopeDto event) {
        String accountId = event.getAccountId() != null ? event.getAccountId() : "ACC-UNKNOWN";
        String deviceId = event.getDeviceId() != null ? event.getDeviceId() : "DEV-UNKNOWN";
        String ipAddress = event.getIpAddress() != null ? event.getIpAddress() : "0.0.0.0";

        // 1. Record velocity metrics in timestamp-aware sliding windows
        long txns1m = redisVelocityService.recordAndCountVelocity(accountId, 60000L);
        long txns5m = redisVelocityService.recordAndCountVelocity(accountId, 300000L);
        long txns1h = redisVelocityService.recordAndCountVelocity(accountId, 3600000L);
        long failedLogins5m = redisVelocityService.recordAndCountFailedAuth(ipAddress, 300000L);
        long accountsOnDevice = redisVelocityService.recordAndCountDeviceAccount(deviceId, accountId);
        long accountsOnIp = redisVelocityService.recordAndCountIpAccount(ipAddress, accountId);

        // Extract transaction amount from payload map if present
        BigDecimal amount = null;
        if (event.getPayload() != null && event.getPayload().containsKey("amount")) {
            Object amtObj = event.getPayload().get("amount");
            if (amtObj != null) {
                amount = new BigDecimal(amtObj.toString());
            }
        }

        // Context flags
        boolean isSuspiciousIp = ipAddress.startsWith("185.220.") || ipAddress.startsWith("45.142.");
        Double deviceAge = event.getPayload() != null && event.getPayload().containsKey("deviceAgeMinutes")
                ? Double.parseDouble(event.getPayload().get("deviceAgeMinutes").toString()) : 120.0;
        Double payeeAge = event.getPayload() != null && event.getPayload().containsKey("beneficiaryAgeMinutes")
                ? Double.parseDouble(event.getPayload().get("beneficiaryAgeMinutes").toString()) : 120.0;
        Boolean impossibleTravel = event.getPayload() != null && event.getPayload().containsKey("impossibleTravel")
                ? Boolean.parseBoolean(event.getPayload().get("impossibleTravel").toString()) : false;

        RuleContext context = RuleContext.builder()
                .event(event)
                .txnsLast1Min(txns1m)
                .txnsLast5Min(txns5m)
                .txnsLast1Hour(txns1h)
                .failedLoginsLast5Min(failedLogins5m)
                .uniqueAccountsOnDevice(accountsOnDevice)
                .uniqueAccountsOnIp(accountsOnIp)
                .currentTransactionAmount(amount)
                .deviceAgeMinutes(deviceAge)
                .beneficiaryAgeMinutes(payeeAge)
                .isSuspiciousIp(isSuspiciousIp)
                .isImpossibleTravel(impossibleTravel)
                .build();

        // 2. Evaluate Rule Engine
        RiskEvaluation evaluation = ruleEngineService.evaluateRisk(context);

        // 3. Generate Fraud Alert if Risk Score >= 61 (HIGH / CRITICAL)
        if (evaluation.getRiskScore() >= 61) {
            triggerFraudAlert(event, evaluation);
        }

        return evaluation;
    }

    private void triggerFraudAlert(EventEnvelopeDto event, RiskEvaluation evaluation) {
        if (fraudAlertRepository.existsByEventId(event.getEventId())) {
            log.warn("Alert Idempotency: Fraud alert already exists for eventId [{}]. Skipping duplicate alert generation.", event.getEventId());
            return;
        }

        try {
            String alertId = "ALT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String triggeredRulesJson = objectMapper.writeValueAsString(evaluation.getTriggeredEvidence());
            String evidenceJson = objectMapper.writeValueAsString(Map.of(
                    "riskScore", evaluation.getRiskScore(),
                    "severity", evaluation.getSeverity(),
                    "evidenceItems", evaluation.getTriggeredEvidence()
            ));

            FraudAlertEntity alertEntity = FraudAlertEntity.builder()
                    .id(alertId)
                    .eventId(event.getEventId())
                    .accountId(event.getAccountId() != null ? event.getAccountId() : "ACC-UNKNOWN")
                    .alertType(evaluation.getSeverity().equals("CRITICAL") ? "CORRELATED_FRAUD_ALERT" : "HIGH_RISK_TRANSACTION")
                    .severity(evaluation.getSeverity())
                    .riskScore(evaluation.getRiskScore())
                    .triggeredRulesJson(triggeredRulesJson)
                    .evidenceJson(evidenceJson)
                    .status("NEW")
                    .build();

            fraudAlertRepository.save(alertEntity);
            log.info("Fraud Alert [{}] created for eventId [{}] with risk score [{}]", alertId, event.getEventId(), evaluation.getRiskScore());

            // Publish to Kafka sentinel.alerts.fraud
            kafkaProducerService.sendFraudAlert(alertEntity, alertEntity.getAccountId());

            // Broadcast via WebSocket STOMP to React frontend
            messagingTemplate.convertAndSend("/topic/alerts", alertEntity);

        } catch (DataIntegrityViolationException ex) {
            log.warn("Race condition alert idempotency catch for eventId [{}]", event.getEventId());
        } catch (Exception ex) {
            log.error("Failed to generate fraud alert for eventId [{}]", event.getEventId(), ex);
        }
    }

    public Page<FraudAlertEntity> getAlerts(String statusFilter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
            return fraudAlertRepository.findByStatusOrderByCreatedAtDesc(statusFilter.toUpperCase(), pageable);
        }
        return fraudAlertRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional
    public FraudAlertEntity updateAlertStatus(String alertId, String newStatus) {
        FraudAlertEntity alert = fraudAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));

        String currentStatus = alert.getStatus();
        validateStatusTransition(currentStatus, newStatus.toUpperCase());

        alert.setStatus(newStatus.toUpperCase());
        log.info("Updated Fraud Alert [{}] status from [{}] to [{}]", alertId, currentStatus, newStatus);
        return fraudAlertRepository.save(alert);
    }

    private void validateStatusTransition(String current, String target) {
        if (current.equals(target)) return;

        boolean isValid = switch (current) {
            case "NEW" -> target.equals("ACKNOWLEDGED") || target.equals("INVESTIGATING") || target.equals("RESOLVED") || target.equals("FALSE_POSITIVE");
            case "ACKNOWLEDGED" -> target.equals("INVESTIGATING") || target.equals("RESOLVED") || target.equals("FALSE_POSITIVE");
            case "INVESTIGATING" -> target.equals("RESOLVED") || target.equals("FALSE_POSITIVE");
            case "RESOLVED", "FALSE_POSITIVE" -> target.equals("INVESTIGATING"); // Reopen
            default -> false;
        };

        if (!isValid) {
            throw new IllegalArgumentException("Invalid status transition from " + current + " to " + target);
        }
    }
}
