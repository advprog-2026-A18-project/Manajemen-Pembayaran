package id.ac.ui.cs.advprog.manajemenpembayaran.security;

import id.ac.ui.cs.advprog.manajemenpembayaran.constant.PaymentConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("paymentAuthorizationService")
public class PaymentAuthorizationService {

    public boolean canAccessOwner(String ownerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> PaymentConstants.Role.ADMIN.equals(authority.getAuthority()));

        return isAdmin || ownerId.equals(authentication.getName());
    }
}
