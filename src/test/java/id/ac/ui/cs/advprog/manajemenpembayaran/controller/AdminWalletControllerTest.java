package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
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
    void topUpAdminWalletShouldReturnUpdatedWallet() throws Exception {
        Wallet wallet = Wallet.builder()
                .ownerId("admin-default")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(350000))
                .build();

        when(adminWalletService.topUpAdminWallet(BigDecimal.valueOf(250000))).thenReturn(wallet);

        String payload = """
                {
                  "amount": 250000
                }
                """;

        mockMvc.perform(post("/api/pembayaran/admin/wallet/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value("admin-default"))
                .andExpect(jsonPath("$.balance").value(350000));
    }
}
