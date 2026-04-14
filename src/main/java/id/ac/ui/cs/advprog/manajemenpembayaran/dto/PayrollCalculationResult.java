package id.ac.ui.cs.advprog.manajemenpembayaran.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PayrollCalculationResult {
    private BigDecimal amount;
    private BigDecimal rateUsed;
    private BigDecimal kgUsed;
    private String formulaDescription;
}
