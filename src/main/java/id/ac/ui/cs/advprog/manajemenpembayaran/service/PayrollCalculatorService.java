package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollCalculationResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PayrollCalculatorService {

    private static final BigDecimal MULTIPLIER = new BigDecimal("0.9");

    public PayrollCalculationResult calculateBuruh(BigDecimal kilogram, BigDecimal ratePerKg) {
        return calculate(kilogram, ratePerKg, "Upah Buruh = rate * kilogram * 90%");
    }

    public PayrollCalculationResult calculateSupir(BigDecimal kilogram, BigDecimal ratePerKg) {
        return calculate(kilogram, ratePerKg, "Upah Supir = rate * kilogram * 90%");
    }

    public PayrollCalculationResult calculateMandor(BigDecimal kilogram, BigDecimal ratePerKg) {
        return calculate(kilogram, ratePerKg, "Upah Mandor = rate * kilogram * 90%");
    }

    private PayrollCalculationResult calculate(BigDecimal kilogram, BigDecimal ratePerKg, String formulaDescription) {
        validatePositive(kilogram, "kilogram");
        validatePositive(ratePerKg, "ratePerKg");

        BigDecimal amount = kilogram
                .multiply(ratePerKg)
                .multiply(MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);

        return PayrollCalculationResult.builder()
                .amount(amount)
                .rateUsed(ratePerKg)
                .kgUsed(kilogram)
                .formulaDescription(formulaDescription)
                .build();
    }

    private void validatePositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
    }
}
