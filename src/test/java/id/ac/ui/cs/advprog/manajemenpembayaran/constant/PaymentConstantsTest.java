package id.ac.ui.cs.advprog.manajemenpembayaran.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentConstantsTest {

    @Test
    void ownerConstantsShouldExposeDefaultOwnerIds() {
        assertEquals("admin-default", PaymentConstants.Owner.ADMIN_DEFAULT);
        assertEquals("mandor-default", PaymentConstants.Owner.MANDOR_DEFAULT);
        assertEquals("buruh-default", PaymentConstants.Owner.BURUH_DEFAULT);
        assertEquals("supir-default", PaymentConstants.Owner.SUPIR_DEFAULT);
    }

    @Test
    void roleConstantsShouldExposeSupportedRoles() {
        assertEquals("ADMIN", PaymentConstants.Role.ADMIN);
        assertEquals("MANDOR", PaymentConstants.Role.MANDOR);
        assertEquals("BURUH", PaymentConstants.Role.BURUH);
        assertEquals("SUPIR", PaymentConstants.Role.SUPIR);
    }

    @Test
    void referenceConstantsShouldExposeTransactionReferenceTypes() {
        assertEquals("TOP_UP_REQUEST", PaymentConstants.Reference.TOP_UP_REQUEST);
        assertEquals("PAYROLL", PaymentConstants.Reference.PAYROLL);
    }
}
