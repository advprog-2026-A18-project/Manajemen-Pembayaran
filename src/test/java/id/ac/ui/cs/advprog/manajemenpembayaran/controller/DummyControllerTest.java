package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.DummyData;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.DummyDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DummyController.class)
class DummyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // PENGGANTI @MockBean di versi baru
    private DummyDataService dummyDataService;

    @Test
    void testGetDummyData() throws Exception {
        DummyData data = DummyData.builder().id("PAY-1").message("Test").status("OK").build();
        when(dummyDataService.getAllData()).thenReturn(Set.of(data));

        mockMvc.perform(get("/api/dummy/data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PAY-1"));
    }
}