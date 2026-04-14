package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollCalculationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayrollCalculatorServiceTest {

    private PayrollCalculatorService payrollCalculatorService;

    @BeforeEach
    void setUp() {
        payrollCalculatorService = new PayrollCalculatorService();
    }

    @Test
    void calculateBuruhShouldReturnExpectedAmount() {
        PayrollCalculationResult result = payrollCalculatorService.calculateBuruh(
                new BigDecimal("100"),
                new BigDecimal("2000")
        );

        assertEquals(new BigDecimal("180000.00"), result.getAmount());
        assertEquals(new BigDecimal("2000"), result.getRateUsed());
        assertEquals(new BigDecimal("100"), result.getKgUsed());
    }

    @Test
    void calculateSupirShouldReturnExpectedAmount() {
        PayrollCalculationResult result = payrollCalculatorService.calculateSupir(
                new BigDecimal("50"),
                new BigDecimal("1500")
        );

        assertEquals(new BigDecimal("67500.00"), result.getAmount());
    }

    @Test
    void calculateMandorShouldReturnExpectedAmount() {
        PayrollCalculationResult result = payrollCalculatorService.calculateMandor(
                new BigDecimal("80"),
                new BigDecimal("2500")
        );

        assertEquals(new BigDecimal("180000.00"), result.getAmount());
    }

    @Test
    void calculateShouldRejectNonPositiveKilogram() {
        assertThrows(IllegalArgumentException.class,
                () -> payrollCalculatorService.calculateBuruh(BigDecimal.ZERO, new BigDecimal("1000")));
    }

    @Test
    void calculateShouldRejectNonPositiveRate() {
        assertThrows(IllegalArgumentException.class,
                () -> payrollCalculatorService.calculateSupir(new BigDecimal("10"), BigDecimal.ZERO));
    }
}
