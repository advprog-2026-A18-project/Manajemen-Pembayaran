package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PayrollStatusTest {

    @Test
    void enumValuesShouldMatchExpectedOrder() {
        assertArrayEquals(
                new PayrollStatus[]{PayrollStatus.PENDING, PayrollStatus.ACCEPTED, PayrollStatus.REJECTED},
                PayrollStatus.values()
        );
    }

    @Test
    void valueOfShouldResolveEnumName() {
        assertEquals(PayrollStatus.PENDING, PayrollStatus.valueOf("PENDING"));
        assertEquals(PayrollStatus.ACCEPTED, PayrollStatus.valueOf("ACCEPTED"));
        assertEquals(PayrollStatus.REJECTED, PayrollStatus.valueOf("REJECTED"));
    }
}
