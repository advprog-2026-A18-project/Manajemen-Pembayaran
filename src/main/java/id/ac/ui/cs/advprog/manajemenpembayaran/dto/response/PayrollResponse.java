package id.ac.ui.cs.advprog.manajemenpembayaran.dto.response;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PayrollResponse {
    private Long id;
    private String ownerId;
    private String ownerRole;
    private BigDecimal kilogram;
    private BigDecimal amount;
    private BigDecimal rateUsed;
    private PayrollStatus status;
    private PayrollSourceType sourceType;
    private String sourceId;
    private String description;
    private String rejectionReason;
    private LocalDateTime createdAt;

    public static PayrollResponse from(Payroll payroll) {
        return PayrollResponse.builder()
                .id(payroll.getId())
                .ownerId(payroll.getOwnerId())
                .ownerRole(payroll.getOwnerRole())
                .kilogram(payroll.getKilogram())
                .amount(payroll.getAmount())
                .rateUsed(payroll.getRateUsed())
                .status(payroll.getStatus())
                .sourceType(payroll.getSourceType())
                .sourceId(payroll.getSourceId())
                .description(payroll.getDescription())
                .rejectionReason(payroll.getRejectionReason())
                .createdAt(payroll.getCreatedAt())
                .build();
    }
}
