package com.pluralsight.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
public class JWTFilter {

    private final String jwtSecret = "YOUR_SECRET_KEY_HERE";
    private final long jwtExpirationMs = 86400000; // 24 hours

    public JWTFilter(TokenProvider tokenProvider) {
    }

    // 🔐 Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 🔐 Convert token → Authentication
    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject(); // email or username
        String role = claims.get("role", String.class); // ROLE_USER

        GrantedAuthority authority =
                new SimpleGrantedAuthority(role);

        return new UsernamePasswordAuthenticationToken(
                username,
                token,
                Collections.singleton(authority)
        );
    }

    // 🔐 Create token (login / register)
    public String createToken(String username, String role) {

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
