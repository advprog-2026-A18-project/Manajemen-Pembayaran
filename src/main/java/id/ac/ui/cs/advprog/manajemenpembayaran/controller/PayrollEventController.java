package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.InternalEventResponse;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollEventProcessResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pembayaran/internal/events")
public class PayrollEventController {

    private final PayrollEventService payrollEventService;

    public PayrollEventController(PayrollEventService payrollEventService) {
        this.payrollEventService = payrollEventService;
    }

    @PostMapping("/harvest-approved")
    public ResponseEntity<InternalEventResponse> processHarvestApproved(@RequestBody HarvestApprovedEvent event) {
        PayrollEventProcessResult result = payrollEventService.processHarvestApprovedWithResult(event);
        return toResponse(result);
    }

    @PostMapping("/shipment-mandor-approved")
    public ResponseEntity<InternalEventResponse> processShipmentMandorApproved(
            @RequestBody ShipmentMandorApprovedEvent event
    ) {
        PayrollEventProcessResult result = payrollEventService.processShipmentMandorApprovedWithResult(event);
        return toResponse(result);
    }

    @PostMapping("/shipment-admin-approved")
    public ResponseEntity<InternalEventResponse> processShipmentAdminApproved(
            @RequestBody ShipmentAdminApprovedEvent event
    ) {
        PayrollEventProcessResult result = payrollEventService.processShipmentAdminApprovedWithResult(event);
        return toResponse(result);
    }

    private ResponseEntity<InternalEventResponse> toResponse(PayrollEventProcessResult result) {
        InternalEventResponse response = InternalEventResponse.builder()
                .payrollId(result.getPayroll().getId())
                .sourceType(result.getPayroll().getSourceType().name())
                .duplicate(!result.isCreated())
                .message(result.isCreated() ? "Payroll created" : "Duplicate event ignored")
                .build();

        return result.isCreated()
                ? ResponseEntity.status(201).body(response)
                : ResponseEntity.ok(response);
    }
}
