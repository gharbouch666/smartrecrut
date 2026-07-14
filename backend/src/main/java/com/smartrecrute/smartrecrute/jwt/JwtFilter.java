package com.smartrecrute.smartrecrute.jwt;

import com.smartrecrute.smartrecrute.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
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
        "/api/auth/reset-password"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }

        if ("GET".equalsIgnoreCase(method) && (
                path.equals("/api/offres/with-tags") ||
                path.equals("/api/offres/open") ||
                path.startsWith("/api/tags")
        )) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
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
                System.out.println("JwtFilter: Loaded user " + username + " with authorities: " + userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("JwtFilter: Set auth for " + request.getMethod() + " " + request.getRequestURI());
            } catch (Exception e) {
                System.out.println("JwtFilter: Auth error: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (isTokenExpired) {
            System.out.println("JwtFilter: Token is expired for " + request.getMethod() + " " + request.getRequestURI());
        } else {
            System.out.println("JwtFilter: No token for " + request.getMethod() + " " + request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
