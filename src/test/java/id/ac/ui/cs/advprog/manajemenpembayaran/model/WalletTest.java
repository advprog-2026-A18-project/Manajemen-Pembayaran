package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void builderAndAccessorsShouldWork() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .ownerId("admin-1")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(100))
                .build();

        assertEquals(1L, wallet.getId());
        assertEquals("admin-1", wallet.getOwnerId());
        assertEquals("ADMIN", wallet.getOwnerRole());
        assertEquals(BigDecimal.valueOf(100), wallet.getBalance());
    }

    @Test
    void onCreateShouldSetZeroWhenBalanceNull() {
        Wallet wallet = Wallet.builder()
                .ownerId("mandor-1")
                .ownerRole("MANDOR")
                .build();

        assertNull(wallet.getBalance());

        wallet.onCreate();

        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void onCreateShouldKeepExistingBalance() {
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(55))
                .build();

        wallet.onCreate();

        assertEquals(BigDecimal.valueOf(55), wallet.getBalance());
    }
}
