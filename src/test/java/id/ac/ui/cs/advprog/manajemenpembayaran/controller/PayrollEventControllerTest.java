package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollEventProcessResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PayrollEventController.class)
class PayrollEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayrollEventService payrollEventService;

    @Test
    void harvestEndpointShouldReturnCreatedWhenNew() throws Exception {
        Payroll payroll = Payroll.builder().id(11L).sourceType(PayrollSourceType.HARVEST_APPROVAL).build();
        when(payrollEventService.processHarvestApprovedWithResult(any(HarvestApprovedEvent.class)))
                .thenReturn(PayrollEventProcessResult.builder().payroll(payroll).created(true).build());

        String payload = """
                {
                  "eventId": "evt-h-1",
                  "buruhId": "buruh-1",
                  "kilogram": 100
                }
                """;

        mockMvc.perform(post("/api/pembayaran/internal/events/harvest-approved")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payrollId").value(11))
                .andExpect(jsonPath("$.duplicate").value(false));
    }

    @Test
    void shipmentMandorEndpointShouldReturnOkWhenDuplicate() throws Exception {
        Payroll payroll = Payroll.builder().id(12L).sourceType(PayrollSourceType.SHIPMENT_MANDOR_APPROVAL).build();
        when(payrollEventService.processShipmentMandorApprovedWithResult(any(ShipmentMandorApprovedEvent.class)))
                .thenReturn(PayrollEventProcessResult.builder().payroll(payroll).created(false).build());

        String payload = """
                {
                  "eventId": "evt-s-1",
                  "supirId": "supir-1",
                  "kilogram": 50
                }
                """;

        mockMvc.perform(post("/api/pembayaran/internal/events/shipment-mandor-approved")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payrollId").value(12))
                .andExpect(jsonPath("$.duplicate").value(true));
    }

    @Test
    void shipmentAdminEndpointShouldReturnCreatedWhenNew() throws Exception {
        Payroll payroll = Payroll.builder().id(13L).sourceType(PayrollSourceType.SHIPMENT_ADMIN_APPROVAL).build();
        when(payrollEventService.processShipmentAdminApprovedWithResult(any(ShipmentAdminApprovedEvent.class)))
                .thenReturn(PayrollEventProcessResult.builder().payroll(payroll).created(true).build());

        String payload = """
                {
                  "eventId": "evt-a-1",
                  "mandorId": "mandor-1",
                  "kilogramDiakui": 80
                }
                """;

        mockMvc.perform(post("/api/pembayaran/internal/events/shipment-admin-approved")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payrollId").value(13))
                .andExpect(jsonPath("$.duplicate").value(false));
    }

    @Test
    void harvestEndpointShouldReturnBadRequestForInvalidEvent() throws Exception {
        when(payrollEventService.processHarvestApprovedWithResult(any(HarvestApprovedEvent.class)))
                .thenThrow(new IllegalArgumentException("eventId is required"));

        String payload = """
                {
                  "eventId": " ",
                  "buruhId": "buruh-1",
                  "kilogram": 100
                }
                """;

        mockMvc.perform(post("/api/pembayaran/internal/events/harvest-approved")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("eventId is required"));
    }
}
