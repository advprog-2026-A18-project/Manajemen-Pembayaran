package id.ac.ui.cs.advprog.manajemenpembayaran.dto.response;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionHistory;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionHistoryResponse {
    private Long id;
    private String ownerId;
    private TransactionType type;
    private BigDecimal amount;
    private String referenceType;
    private String referenceId;
    private LocalDateTime createdAt;

    public static TransactionHistoryResponse from(TransactionHistory transactionHistory) {
        return TransactionHistoryResponse.builder()
                .id(transactionHistory.getId())
                .ownerId(transactionHistory.getOwnerId())
                .type(transactionHistory.getType())
                .amount(transactionHistory.getAmount())
                .referenceType(transactionHistory.getReferenceType())
                .referenceId(transactionHistory.getReferenceId())
                .createdAt(transactionHistory.getCreatedAt())
                .build();
    }
}
