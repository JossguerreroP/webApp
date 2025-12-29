package com.company.sigess.security;

import io.jsonwebtoken.Claims;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // CORS headers
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Handle preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Allow public endpoints
        if (path.equals("/api/login") || path.equals("/api/users/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = JwtUtil.validateToken(token);
                Long userId = Long.parseLong(claims.getSubject());
                String role = claims.get("role", String.class);

                UserPrincipal principal = new UserPrincipal(userId, role);
                SecurityContext.setUser(principal);

                try {
                    chain.doFilter(request, response);
                } finally {
                    SecurityContext.clear();
                }
            } catch (Exception e) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid or expired token");
            }
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Authorization header missing");
        }
    }

    @Override
    public void destroy() {}
}
