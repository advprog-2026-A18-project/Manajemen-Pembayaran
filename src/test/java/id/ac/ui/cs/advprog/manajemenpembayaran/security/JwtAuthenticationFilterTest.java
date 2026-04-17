package id.ac.ui.cs.advprog.manajemenpembayaran.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenJwtUtilsUnavailable() throws ServletException, IOException {
        ObjectProvider<JwtUtils> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSetAuthenticationForValidBearerToken() throws ServletException, IOException {
        ObjectProvider<JwtUtils> provider = mock(ObjectProvider.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        when(provider.getIfAvailable()).thenReturn(jwtUtils);
        when(jwtUtils.validateToken("valid-token")).thenReturn(true);
        when(jwtUtils.getEmailFromToken("valid-token")).thenReturn("user@mysawit.id");
        when(jwtUtils.getRoleFromToken("valid-token")).thenReturn("ADMIN");

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("user@mysawit.id", authentication.getName());
        assertEquals("ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void shouldNotSetAuthenticationForInvalidToken() throws ServletException, IOException {
        ObjectProvider<JwtUtils> provider = mock(ObjectProvider.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        when(provider.getIfAvailable()).thenReturn(jwtUtils);
        when(jwtUtils.validateToken("invalid-token")).thenReturn(false);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
