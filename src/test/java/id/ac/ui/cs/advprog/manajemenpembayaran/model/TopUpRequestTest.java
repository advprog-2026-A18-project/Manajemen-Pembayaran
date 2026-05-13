package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TopUpRequestTest {

    @Test
    void builderAndAccessorsShouldWork() {
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime completedAt = LocalDateTime.now();

        TopUpRequest request = TopUpRequest.builder()
                .id(1L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .status(TopUpStatus.COMPLETED)
                .createdAt(createdAt)
                .completedAt(completedAt)
                .build();

        assertEquals(1L, request.getId());
        assertEquals("admin-default", request.getOwnerId());
        assertEquals(BigDecimal.valueOf(250000), request.getAmount());
        assertEquals(TopUpStatus.COMPLETED, request.getStatus());
        assertEquals(createdAt, request.getCreatedAt());
        assertEquals(completedAt, request.getCompletedAt());
    }

    @Test
    void onCreateShouldSetPendingStatusAndCreatedAt() {
        TopUpRequest request = TopUpRequest.builder()
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .build();

        assertNull(request.getStatus());
        assertNull(request.getCreatedAt());

        request.onCreate();

        assertEquals(TopUpStatus.PENDING, request.getStatus());
        assertNotNull(request.getCreatedAt());
    }

    @Test
    void onCreateShouldKeepExistingStatusAndCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        TopUpRequest request = TopUpRequest.builder()
                .status(TopUpStatus.FAILED)
                .createdAt(createdAt)
                .build();

        request.onCreate();

        assertEquals(TopUpStatus.FAILED, request.getStatus());
        assertEquals(createdAt, request.getCreatedAt());
    }
}
