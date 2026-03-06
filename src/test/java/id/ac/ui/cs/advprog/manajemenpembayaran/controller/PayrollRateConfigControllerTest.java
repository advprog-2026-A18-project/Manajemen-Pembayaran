package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollRateRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollRateConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PayrollRateConfigController.class)
class PayrollRateConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayrollRateConfigService payrollRateConfigService;

    @Test
    void upsertRatesShouldReturnOk() throws Exception {
        PayrollRateConfig config = PayrollRateConfig.builder()
                .id(1L)
                .buruhRatePerKg(BigDecimal.valueOf(2))
                .supirRatePerKg(BigDecimal.valueOf(3))
                .mandorRatePerKg(BigDecimal.valueOf(4))
                .build();

        when(payrollRateConfigService.upsert(any(PayrollRateRequest.class))).thenReturn(config);

        String payload = """
                {
                  "buruhRatePerKg": 2,
                  "supirRatePerKg": 3,
                  "mandorRatePerKg": 4
                }
                """;

        mockMvc.perform(put("/api/pembayaran/admin/rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buruhRatePerKg").value(2));
    }

    @Test
    void upsertRatesShouldReturnBadRequestForInvalidInput() throws Exception {
        String payload = """
                {
                  "buruhRatePerKg": 0,
                  "supirRatePerKg": 3,
                  "mandorRatePerKg": 4
                }
                """;

        mockMvc.perform(put("/api/pembayaran/admin/rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getCurrentRateShouldReturnNoContentWhenMissing() throws Exception {
        when(payrollRateConfigService.getCurrentRate()).thenReturn(null);

        mockMvc.perform(get("/api/pembayaran/admin/rates"))
                .andExpect(status().isNoContent());
    }
}
