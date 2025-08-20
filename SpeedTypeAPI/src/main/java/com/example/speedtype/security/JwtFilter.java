package com.example.speedtype.security;

import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("JwtFilter: Processing " + method + " request to " + requestURI);
        System.out.println("JwtFilter: Authorization header: " + (authHeader != null ? authHeader.substring(0, Math.min(authHeader.length(), 30)) + "..." : "null"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                System.out.println("JwtFilter: Extracted username: " + username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("JwtFilter: Loaded user details for: " + userDetails.getUsername());

                    if (jwtUtil.validateToken(token, username)) {
                        System.out.println("JwtFilter: Token is valid, setting authentication");
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContext context = SecurityContextHolder.getContext();
                        context.setAuthentication(authToken);
                        System.out.println("JwtFilter: Authentication set successfully for " + username);
                    } else {
                        System.out.println("JwtFilter: Token validation failed");
                    }
                }
            } catch (JwtException e) {
                System.out.println("JwtFilter: JWT Exception: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        } else {
            System.out.println("JwtFilter: No Bearer token found");
        }

        chain.doFilter(request, response);
    }
}
