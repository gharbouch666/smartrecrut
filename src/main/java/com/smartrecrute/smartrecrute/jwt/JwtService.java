package com.smartrecrute.smartrecrute.jwt;

import com.smartrecrute.smartrecrute.entity.Utilisateur;
import com.smartrecrute.smartrecrute.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Slf4j
@EnableScheduling
@Transactional
@Service
public class JwtService {

    private final String ENCRIPTION_KEY = "608f36e92dc66d97d5933f0e6371493cb4fc05b1aa8f8de64014732472303a7c";
    private AuthService userService;
    private JwtRepository jwtRepository;

    public Jwt tokenByValeur(String valeur) {
        return jwtRepository.findByValue(valeur)
                .orElseThrow(() -> new RuntimeException("no token"));
    }

    public Map<String, String> generate(String username) {
        Utilisateur utilisateur = (Utilisateur) userService.loadUserByUsername(username);
        this.disableTokens(utilisateur);
        final Map<String, String> jwtMap = generateJwt(utilisateur);
        final Jwt jwt = Jwt.builder()
                .desactivated(false)
                .expired(false)
                .value(jwtMap.get("accessToken"))
                .user(utilisateur)
                .build();
        jwtRepository.save(jwt);

        return jwtMap;
    }

    public void disableTokens(Utilisateur utilisateur) {
        final List<Jwt> jwtList = jwtRepository.findTokensByUserEmail(utilisateur.getEmail()).map(
                jwt -> {
                    jwt.setDesactivated(true);
                    jwt.setExpired(true);
                    return jwt;
                }

        ).collect(Collectors.toList());
        jwtRepository.saveAll(jwtList);
    }

    public Map<String, String> generateJwt(Utilisateur utilisateur) {
        final long currentTime = System.currentTimeMillis();
        final long accessExpiration = currentTime + (300 * 60 * 1000); // 300 minutes 5h
        final long refreshExpiration = currentTime + (24 * 60 * 60 * 1000); // 24 heures

        // Get role from entity - handle both enum and custom implementations
        String role = utilisateur.getClass().getSimpleName();
        
        final String accessToken = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(accessExpiration))
                .setSubject(utilisateur.getEmail())
                .addClaims(Map.of(
                        "nom", utilisateur.getNom(),
                        "type", "access",
                        "role", role
                ))
                .claim("roles", List.of("ROLE_" + role))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        final String refreshToken = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(refreshExpiration))
                .setSubject(utilisateur.getEmail())
                .addClaims(Map.of(
                        "nom", utilisateur.getNom(),
                        "type", "refresh"
                ))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    public Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    public <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Jwt jwt = jwtRepository.findValidToken(utilisateur.getEmail(), false, false)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        jwt.setDesactivated(true);
        jwt.setExpired(true);
        jwtRepository.save(jwt);
    }

    public boolean isRefreshToken(String token) {
        String type = getClaim(token, claims -> claims.get("type", String.class));
        return "refresh".equals(type);
    }
}