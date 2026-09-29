package org.example.conta.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private final Key key;
    private final long expirationMs = 86400000; // 24 horas

    public JwtTokenService(@Value("${jwt.secret:bWluaGEtY2hhdmUtc2VjcmV0YS1zdXBlci1zZWd1cmEtcGFyYS1vLWJhbWNvLTEyMzQ1Ng==}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long clienteId, String nome) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(nome)
                .claim("clienteId", clienteId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Long getClienteId(String token) {
        Claims claims = getClaims(token);
        Object clienteId = claims.get("clienteId");
        if (clienteId instanceof Number num) {
            return num.longValue();
        } else if (clienteId != null) {
            return Long.valueOf(clienteId.toString());
        }
        return null;
    }
}
