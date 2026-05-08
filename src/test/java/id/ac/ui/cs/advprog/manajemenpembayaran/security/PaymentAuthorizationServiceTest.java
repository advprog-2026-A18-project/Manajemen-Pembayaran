package id.ac.ui.cs.advprog.manajemenpembayaran.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentAuthorizationServiceTest {

    private final PaymentAuthorizationService paymentAuthorizationService = new PaymentAuthorizationService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canAccessOwnerShouldAllowAdminToAccessAnyOwner() {
        authenticate("admin-default", "ADMIN");

        assertTrue(paymentAuthorizationService.canAccessOwner("buruh-1"));
    }

    @Test
    void canAccessOwnerShouldAllowMatchingOwner() {
        authenticate("buruh-1", "BURUH");

        assertTrue(paymentAuthorizationService.canAccessOwner("buruh-1"));
    }

    @Test
    void canAccessOwnerShouldRejectDifferentOwner() {
        authenticate("buruh-1", "BURUH");

        assertFalse(paymentAuthorizationService.canAccessOwner("buruh-2"));
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
