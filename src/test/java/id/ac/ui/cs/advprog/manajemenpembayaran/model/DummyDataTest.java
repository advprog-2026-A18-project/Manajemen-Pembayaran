package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DummyDataTest {

    @Test
    void testBuilderAndGetters() {
        DummyData data = DummyData.builder()
                .id("PAY-001")
                .message("Pembayaran berhasil")
                .status("SUCCESS")
                .build();
        assertEquals("PAY-001", data.getId());
        assertEquals("Pembayaran berhasil", data.getMessage());
        assertEquals("SUCCESS", data.getStatus());
    }

    @Test
    void testSetters() {
        DummyData data = DummyData.builder().build();

        data.setId("PAY-002");
        data.setMessage("Menunggu konfirmasi mandor");
        data.setStatus("PENDING");

        assertEquals("PAY-002", data.getId());
        assertEquals("Menunggu konfirmasi mandor", data.getMessage());
        assertEquals("PENDING", data.getStatus());
    }
}