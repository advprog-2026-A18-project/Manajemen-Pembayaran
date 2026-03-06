package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PaymentReadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentReadController.class)
class PaymentReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentReadService paymentReadService;

    @Test
    void getPayrollsShouldReturnData() throws Exception {
        Payroll payroll = Payroll.builder()
                .id(1L)
                .ownerId("u1")
                .status(PayrollStatus.PENDING)
                .build();

        when(paymentReadService.getPayrolls("u1", PayrollStatus.PENDING)).thenReturn(List.of(payroll));

        mockMvc.perform(get("/api/pembayaran/payrolls")
                        .param("ownerId", "u1")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerId").value("u1"));
    }

    @Test
    void getWalletShouldReturnWallet() throws Exception {
        Wallet wallet = Wallet.builder()
                .ownerId("u2")
                .ownerRole("BURUH")
                .balance(BigDecimal.valueOf(20))
                .build();

        when(paymentReadService.getWalletByOwnerId("u2")).thenReturn(wallet);

        mockMvc.perform(get("/api/pembayaran/wallets/u2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value("u2"));
    }

    @Test
    void getWalletShouldReturnNotFoundWhenMissing() throws Exception {
        when(paymentReadService.getWalletByOwnerId("missing"))
                .thenThrow(new ResourceNotFoundException("Wallet not found for ownerId=missing"));

        mockMvc.perform(get("/api/pembayaran/wallets/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Wallet not found for ownerId=missing"));
    }
}
