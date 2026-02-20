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

    @Test
    void testEqualsAndHashCode() {
        DummyData data1 = DummyData.builder().id("1").build();
        DummyData data2 = DummyData.builder().id("1").build();
        DummyData data3 = DummyData.builder().id("2").build();

        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assert(data1.equals(data2));
        assert(!data1.equals(data3));
    }

    @Test
    void testNoArgsConstructor() {
        DummyData data = new DummyData();
        assertEquals(null, data.getId());
    }
}