package com.sentinelx.ingestion.service;

import com.sentinelx.ingestion.config.KafkaConfig;
import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a financial event using accountId as partition key.
     */
    public void sendFinancialEvent(EventEnvelopeDto event) {
        String partitionKey = event.getAccountId() != null ? event.getAccountId() : event.getEventId();
        log.info("Publishing Financial Event [{}] to topic [{}] with key [{}]", event.getEventId(), KafkaConfig.TOPIC_FINANCIAL_EVENTS, partitionKey);
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KafkaConfig.TOPIC_FINANCIAL_EVENTS, partitionKey, event);
        future.whenComplete((result, ex) -> {
            if (ex == null && result != null) {
                log.debug("Event [{}] successfully published to partition [{}]", event.getEventId(), result.getRecordMetadata().partition());
            } else {
                log.error("Failed to publish event [{}] to Kafka", event.getEventId(), ex);
            }
        });
    }

    /**
     * Publishes a security event using ipAddress as partition key.
     */
    public void sendSecurityEvent(EventEnvelopeDto event) {
        String partitionKey = event.getIpAddress() != null ? event.getIpAddress() : event.getEventId();
        log.info("Publishing Security Event [{}] to topic [{}] with key [{}]", event.getEventId(), KafkaConfig.TOPIC_SECURITY_EVENTS, partitionKey);
        
        kafkaTemplate.send(KafkaConfig.TOPIC_SECURITY_EVENTS, partitionKey, event);
    }

    /**
     * Publishes a failed/exhausted event to Dead Letter Queue (DLQ).
     */
    public void sendToDlq(EventEnvelopeDto event, String errorMessage, String originalTopic) {
        log.warn("Routing Event [{}] from topic [{}] to DLQ. Reason: {}", event.getEventId(), originalTopic, errorMessage);
        kafkaTemplate.send(KafkaConfig.TOPIC_DLQ_EVENTS, event.getEventId(), event);
    }

    /**
     * Publishes a Fraud Alert event to sentinel.alerts.fraud topic using accountId as partition key.
     */
    public void sendFraudAlert(Object alertPayload, String accountId) {
        String partitionKey = accountId != null ? accountId : "GLOBAL";
        log.info("Publishing Fraud Alert to topic [{}] with key [{}]", KafkaConfig.TOPIC_FRAUD_ALERTS, partitionKey);
        kafkaTemplate.send(KafkaConfig.TOPIC_FRAUD_ALERTS, partitionKey, alertPayload);
    }
}
