package com.sentinelx.ingestion.service;

import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPublisherService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastLiveEvent(EventEnvelopeDto event) {
        log.info("Broadcasting Event [{}] over WebSocket destination [/topic/telemetry]", event.getEventId());
        messagingTemplate.convertAndSend("/topic/telemetry", event);
    }
}
