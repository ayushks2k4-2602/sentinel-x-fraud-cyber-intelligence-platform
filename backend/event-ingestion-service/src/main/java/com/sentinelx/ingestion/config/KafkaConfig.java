package com.sentinelx.ingestion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_FINANCIAL_EVENTS = "sentinel.events.financial";
    public static final String TOPIC_SECURITY_EVENTS = "sentinel.events.security";
    public static final String TOPIC_DLQ_EVENTS = "sentinel.events.dlq";
    public static final String TOPIC_FRAUD_ALERTS = "sentinel.alerts.fraud";

    @Bean
    public NewTopic financialEventsTopic() {
        return TopicBuilder.name(TOPIC_FINANCIAL_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic securityEventsTopic() {
        return TopicBuilder.name(TOPIC_SECURITY_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic dlqEventsTopic() {
        return TopicBuilder.name(TOPIC_DLQ_EVENTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic fraudAlertsTopic() {
        return TopicBuilder.name(TOPIC_FRAUD_ALERTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
