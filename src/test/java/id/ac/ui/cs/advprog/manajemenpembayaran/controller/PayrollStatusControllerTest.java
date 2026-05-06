package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.security.JwtUtils;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
}
