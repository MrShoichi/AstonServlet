package ru.shoichi.films.jwt;

import static org.mockito.Mockito.*;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private FilterChain chainMock;

    private final JwtFilter jwtFilter = new JwtFilter();

    private static final String EXCLUDED_PATHS = "/exclude";
    private static final String SECURE_PATH = "/include";

    @BeforeEach
    public void setUp() {
        when(filterConfig.getInitParameter("excludedPaths")).thenReturn(EXCLUDED_PATHS);
        jwtFilter.init(filterConfig);
    }

    @Test
    public void testExcludedPaths() throws Exception {
        when(requestMock.getRequestURI()).thenReturn(EXCLUDED_PATHS);
        jwtFilter.doFilter(requestMock, responseMock, chainMock);
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testTokenNotFound() throws Exception {
        when(requestMock.getHeader("Authorization")).thenReturn(null);
        when(requestMock.getCookies()).thenReturn(null);
        when(requestMock.getRequestURI()).thenReturn(SECURE_PATH);
        jwtFilter.doFilter(requestMock, responseMock, chainMock);
        verify(responseMock, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        verify(chainMock, times(0)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testInvalidToken() throws Exception {
        String invalidToken = "invalid_token";
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            when(requestMock.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
            when(JwtUtil.validateToken(invalidToken)).thenReturn(false);
            when(requestMock.getRequestURI()).thenReturn(SECURE_PATH);
            jwtFilter.doFilter(requestMock, responseMock, chainMock);
            verify(responseMock, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            verify(chainMock, times(0)).doFilter(requestMock, responseMock);
        }
    }

    @Test
    public void testValidTokenAndAccess() throws Exception {
        String validToken = "valid_token";
        String userRole = "Admin";
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            when(requestMock.getHeader("Authorization")).thenReturn(validToken);
            when(JwtUtil.validateToken(validToken)).thenReturn(true);
            when(JwtUtil.getClaimFromToken(validToken, "role")).thenReturn(userRole);
            when(requestMock.getRequestURI()).thenReturn(SECURE_PATH);
            jwtFilter.doFilter(requestMock, responseMock, chainMock);
            verify(chainMock, times(1)).doFilter(requestMock, responseMock);
        }
    }

    @Test
    public void testForbiddenAccess() throws Exception {
        String validToken = "valid_token";
        String userRole = "User";
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            when(requestMock.getHeader("Authorization")).thenReturn(validToken);
            when(JwtUtil.validateToken(validToken)).thenReturn(true);
            when(JwtUtil.getClaimFromToken(validToken, "role")).thenReturn(userRole);
            when(requestMock.getRequestURI()).thenReturn(SECURE_PATH);
            when(requestMock.getMethod()).thenReturn("POST");

            jwtFilter.doFilter(requestMock, responseMock, chainMock);
            verify(responseMock, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            verify(chainMock, times(0)).doFilter(requestMock, responseMock);
        }
    }
}
