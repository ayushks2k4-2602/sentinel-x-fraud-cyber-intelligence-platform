package com.sentinelx.ingestion;

import com.sentinelx.ingestion.model.FraudAlertEntity;
import com.sentinelx.ingestion.repository.FraudAlertRepository;
import com.sentinelx.ingestion.service.FraudAlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertLifecycleTest {

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @InjectMocks
    private FraudAlertService fraudAlertService;

    @Test
    void testValidAlertLifecycleTransitions() {
        FraudAlertEntity alert = FraudAlertEntity.builder()
                .id("ALT-8801")
                .eventId("EVT-1001")
                .status("NEW")
                .build();

        when(fraudAlertRepository.findById("ALT-8801")).thenReturn(Optional.of(alert));
        when(fraudAlertRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // NEW -> ACKNOWLEDGED
        FraudAlertEntity res1 = fraudAlertService.updateAlertStatus("ALT-8801", "ACKNOWLEDGED");
        assertEquals("ACKNOWLEDGED", res1.getStatus());

        // ACKNOWLEDGED -> INVESTIGATING
        FraudAlertEntity res2 = fraudAlertService.updateAlertStatus("ALT-8801", "INVESTIGATING");
        assertEquals("INVESTIGATING", res2.getStatus());

        // INVESTIGATING -> RESOLVED
        FraudAlertEntity res3 = fraudAlertService.updateAlertStatus("ALT-8801", "RESOLVED");
        assertEquals("RESOLVED", res3.getStatus());
    }

    @Test
    void testInvalidStatusTransition_ThrowsException() {
        FraudAlertEntity alert = FraudAlertEntity.builder()
                .id("ALT-8802")
                .eventId("EVT-1002")
                .status("RESOLVED")
                .build();

        when(fraudAlertRepository.findById("ALT-8802")).thenReturn(Optional.of(alert));

        // RESOLVED -> ACKNOWLEDGED is invalid
        assertThrows(IllegalArgumentException.class, () -> {
            fraudAlertService.updateAlertStatus("ALT-8802", "ACKNOWLEDGED");
        });
    }
}
