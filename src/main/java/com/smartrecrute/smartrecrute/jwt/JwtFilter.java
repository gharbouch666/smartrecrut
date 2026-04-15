package com.smartrecrute.smartrecrute.jwt;

import com.smartrecrute.smartrecrute.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthService userService;
    
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/candidatures",
        "/api/offres/open",
        "/api/offres/with-tags",
        "/api/tags",
        "/api/stats",
        "/api/candidatures/apply"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATHS.contains(path); // Only skip public paths
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        
        String token = null;
        String username = null;
        boolean isTokenExpired = true;

        final String authorization = request.getHeader("Authorization");
        
        try {
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                isTokenExpired = jwtService.isTokenExpired(token);
                username = jwtService.extractUsername(token);
            }
        } catch (Exception e) {
            System.out.println("JwtFilter: Token parsing error: " + e.getMessage());
        }

        if (!isTokenExpired && username != null) {
            try {
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                System.out.println("JwtFilter: Auth error: " + e.getMessage());
            }
        } else if (isTokenExpired) {
            System.out.println("JwtFilter: Token is expired");
        }

        filterChain.doFilter(request, response);
    }
}
