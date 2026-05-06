package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.security.JwtUtils;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PayrollStatusController.class)
@AutoConfigureMockMvc(addFilters = false)
class PayrollStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayrollStatusService payrollStatusService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void acceptPayrollShouldReturnAcceptedPayroll() throws Exception {
        Payroll payroll = Payroll.builder()
                .id(1L)
                .ownerId("buruh-1")
                .ownerRole("BURUH")
                .amount(BigDecimal.valueOf(180000))
                .status(PayrollStatus.ACCEPTED)
                .build();

        when(payrollStatusService.acceptPayroll(1L)).thenReturn(payroll);

        mockMvc.perform(post("/api/pembayaran/admin/payrolls/1/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void rejectPayrollShouldReturnRejectedPayroll() throws Exception {
        Payroll payroll = Payroll.builder()
                .id(2L)
                .ownerId("buruh-2")
                .ownerRole("BURUH")
                .amount(BigDecimal.valueOf(144000))
                .status(PayrollStatus.REJECTED)
                .rejectionReason("Incorrect harvest data")
                .build();

        when(payrollStatusService.rejectPayroll(2L, "Incorrect harvest data")).thenReturn(payroll);

        String payload = """
                {
                  "rejectionReason": "Incorrect harvest data"
                }
                """;

        mockMvc.perform(post("/api/pembayaran/admin/payrolls/2/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason").value("Incorrect harvest data"));
    }

    @Test
    void payPayrollShouldReturnPaidPayroll() throws Exception {
        Payroll payroll = Payroll.builder()
                .id(3L)
                .ownerId("buruh-3")
                .ownerRole("BURUH")
                .amount(BigDecimal.valueOf(108000))
                .status(PayrollStatus.PAID)
                .build();

        when(payrollStatusService.payPayroll(3L)).thenReturn(payroll);

        mockMvc.perform(post("/api/pembayaran/admin/payrolls/3/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}
