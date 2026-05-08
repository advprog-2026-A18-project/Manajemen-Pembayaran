package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.security.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.manajemenpembayaran.security.JwtUtils;
import id.ac.ui.cs.advprog.manajemenpembayaran.security.PaymentAuthorizationService;
import id.ac.ui.cs.advprog.manajemenpembayaran.security.SecurityConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PaymentReadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentReadController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, PaymentAuthorizationService.class})
class PaymentReadAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentReadService paymentReadService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getWalletShouldRejectDifferentOwner() throws Exception {
        authenticate("buruh-1", "BURUH");

        mockMvc.perform(get("/api/pembayaran/wallets/buruh-2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPayrollsShouldRejectDifferentOwner() throws Exception {
        authenticate("buruh-1", "BURUH");

        mockMvc.perform(get("/api/pembayaran/payrolls")
                        .param("ownerId", "buruh-2"))
                .andExpect(status().isForbidden());
    }

    private void authenticate(String ownerId, String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        ownerId,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                )
        );
    }
}
