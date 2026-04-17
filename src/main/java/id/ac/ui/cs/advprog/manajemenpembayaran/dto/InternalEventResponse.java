package id.ac.ui.cs.advprog.manajemenpembayaran.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InternalEventResponse {
    private Long payrollId;
    private String sourceType;
    private boolean duplicate;
    private String message;
}
