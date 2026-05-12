package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.security.JwtUtils;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.AdminWalletService;
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

@WebMvcTest(AdminWalletController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminWalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminWalletService adminWalletService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void topUpAdminWalletShouldReturnPendingTopUpRequest() throws Exception {
        TopUpRequest request = TopUpRequest.builder()
                .id(1L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .status(TopUpStatus.PENDING)
                .build();

        when(adminWalletService.createTopUpRequest(BigDecimal.valueOf(250000))).thenReturn(request);

        String payload = """
                {
                  "amount": 250000
                }
                """;

        mockMvc.perform(post("/api/pembayaran/admin/wallet/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerId").value("admin-default"))
                .andExpect(jsonPath("$.amount").value(250000))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void confirmTopUpRequestShouldReturnCompletedTopUpRequest() throws Exception {
        TopUpRequest request = TopUpRequest.builder()
                .id(2L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .status(TopUpStatus.COMPLETED)
                .build();

        when(adminWalletService.confirmTopUpRequest(2L)).thenReturn(request);

        mockMvc.perform(post("/api/pembayaran/admin/wallet/top-up/2/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.ownerId").value("admin-default"))
                .andExpect(jsonPath("$.amount").value(250000))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
