package id.ac.ui.cs.advprog.manajemenpembayaran.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayrollRateRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "must be greater than 0")
    private BigDecimal buruhRatePerKg;

    @NotNull
    @DecimalMin(value = "0.01", message = "must be greater than 0")
    private BigDecimal supirRatePerKg;

    @NotNull
    @DecimalMin(value = "0.01", message = "must be greater than 0")
    private BigDecimal mandorRatePerKg;
}
