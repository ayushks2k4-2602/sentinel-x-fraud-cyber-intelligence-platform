package com.sentinelx.ingestion.service;

import com.sentinelx.ingestion.config.KafkaConfig;
import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final IdempotencyService idempotencyService;
    private final WebSocketPublisherService webSocketPublisherService;
    private final KafkaProducerService kafkaProducerService;
    private final FraudAlertService fraudAlertService;

    @KafkaListener(topics = {KafkaConfig.TOPIC_FINANCIAL_EVENTS, KafkaConfig.TOPIC_SECURITY_EVENTS}, groupId = "sentinel-ingestion-group")
    public void consumeEvent(@Payload EventEnvelopeDto event) {
        log.info("Kafka Consumer Received Event [{}] of type [{}]", event.getEventId(), event.getEventType());

        try {
            // 1. Idempotency Check & Persistence
            boolean isNewEvent = idempotencyService.processAndPersist(event);
            
            if (isNewEvent) {
                // 2. Evaluate Rule Engine & Generate Fraud Alert if risk >= 61
                if ("TRANSACTION".equalsIgnoreCase(event.getEventType()) || "IMPS_TRANSFER".equalsIgnoreCase(event.getEventType())) {
                    fraudAlertService.evaluateAndAlert(event);
                }

                // 3. Broadcast live event to React SOC Dashboard
                webSocketPublisherService.broadcastLiveEvent(event);
            } else {
                log.debug("Skipping downstream processing for duplicate event [{}]", event.getEventId());
            }
        } catch (Exception ex) {
            log.error("Kafka Consumer failed processing event [{}]. Routing to DLQ...", event.getEventId(), ex);
            kafkaProducerService.sendToDlq(event, ex.getMessage(), KafkaConfig.TOPIC_FINANCIAL_EVENTS);
        }
    }
}
