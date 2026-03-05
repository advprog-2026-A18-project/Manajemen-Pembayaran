package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PayrollRateConfigTest {

    @Test
    void builderAndAccessorsShouldWork() {
        LocalDateTime effectiveFrom = LocalDateTime.now();
        PayrollRateConfig config = PayrollRateConfig.builder()
                .id(1L)
                .buruhRatePerKg(BigDecimal.valueOf(2))
                .supirRatePerKg(BigDecimal.valueOf(3))
                .mandorRatePerKg(BigDecimal.valueOf(4))
                .effectiveFrom(effectiveFrom)
                .build();

        assertEquals(1L, config.getId());
        assertEquals(BigDecimal.valueOf(2), config.getBuruhRatePerKg());
        assertEquals(BigDecimal.valueOf(3), config.getSupirRatePerKg());
        assertEquals(BigDecimal.valueOf(4), config.getMandorRatePerKg());
        assertEquals(effectiveFrom, config.getEffectiveFrom());
    }

    @Test
    void onCreateShouldSetEffectiveFromWhenNull() {
        PayrollRateConfig config = PayrollRateConfig.builder().build();

        assertNull(config.getEffectiveFrom());

        config.onCreate();

        assertNotNull(config.getEffectiveFrom());
    }

    @Test
    void onCreateShouldKeepExistingEffectiveFrom() {
        LocalDateTime existing = LocalDateTime.now().minusHours(2);
        PayrollRateConfig config = PayrollRateConfig.builder()
                .effectiveFrom(existing)
                .build();

        config.onCreate();

        assertEquals(existing, config.getEffectiveFrom());
    }
}
