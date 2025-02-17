package ru.shoichi.films.jwt;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.shoichi.films.jwt.JwtCookieUtil.getTokenFromCookies;

@WebFilter(value = "/api/v1/*",
        initParams = {
            @WebInitParam(name = "excludedPaths", value = "/api/v1/users/login,/api/v1/users/register"),
            @WebInitParam(name = "excludedPathPost", value = "/api/v1/reviews")
        }
)
public class JwtFilter implements Filter {
    private List<String> excludedPaths = new ArrayList<>();
    private List<String> excludedPathsPost = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        String excludedPathsParam = filterConfig.getInitParameter("excludedPaths");
        if (excludedPathsParam != null) {
            excludedPaths = Arrays.asList(excludedPathsParam.split(","));
        }
        String excludedPathsPostParam = filterConfig.getInitParameter("excludedPathPost");
        if (excludedPathsPostParam != null) {
            excludedPathsPost = Arrays.asList(excludedPathsPostParam.split(","));

        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if (excludedPaths.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            token = getTokenFromCookies(httpRequest);
        }
        if (token == null || !isValidToken(token)) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String userRole = getRoleFromToken(token);
        if (userRole == null || !hasAccess(userRole, httpRequest)) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean hasAccess(String userRole, HttpServletRequest httpRequest) {
        String method = httpRequest.getMethod();
        if(excludedPathsPost.contains(httpRequest.getRequestURI())) {
            return true;
        }
        if (!"Admin".equals(userRole)) {
            return !"POST".equals(method) && !"PUT".equals(method) && !"DELETE".equals(method);
        }

        return true;
    }

    private String getRoleFromToken(String token) {
        return JwtUtil.getClaimFromToken(token, "role");
    }

    private boolean isValidToken(String token) {
        return JwtUtil.validateToken(token);
    }



}
