package id.ac.ui.cs.advprog.manajemenpembayaran.dto;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayrollEventProcessResult {
    private Payroll payroll;
    private boolean created;
}
