// JwtUtil.java
package com.thegoalgrid.goalgrid.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long jwtExpirationMs = 86400000; // 24 hours

    public String generateJwtToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject((username))
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getUserIdFromJwtToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            parseClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Log the exception message if necessary
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
