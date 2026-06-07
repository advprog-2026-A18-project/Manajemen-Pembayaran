package id.ac.ui.cs.advprog.manajemenpembayaran.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_ID_HEADER = "X-User-Id";

    private final ObjectProvider<JwtUtils> jwtUtilsProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        JwtUtils jwtUtils = jwtUtilsProvider.getIfAvailable();
        if (jwtUtils == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                authenticate(request, resolvePrincipal(jwtUtils.getIdFromToken(jwt), jwtUtils.getEmailFromToken(jwt)),
                        jwtUtils.getRoleFromToken(jwt));
            } else {
                authenticateFromGatewayHeaders(request);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateFromGatewayHeaders(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID_HEADER);
        String email = request.getHeader(USER_EMAIL_HEADER);
        String role = request.getHeader(USER_ROLE_HEADER);
        String principal = resolvePrincipal(userId, email);

        if (principal != null && role != null && !role.isBlank()) {
            authenticate(request, principal, role);
        }
    }

    private String resolvePrincipal(String userId, String email) {
        if (userId != null && !userId.isBlank()) {
            return userId;
        }
        if (email != null && !email.isBlank()) {
            return email;
        }
        return null;
    }

    private void authenticate(HttpServletRequest request, String principal, String role) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(normalizeRole(role));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                principal, null, Collections.singletonList(authority)
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private String normalizeRole(String role) {
        return role.startsWith("ROLE_") ? role.substring(5) : role;
    }
}
