package org.example.transferencia.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private final Key key;

    public JwtTokenService(@Value("${jwt.secret:bWluaGEtY2hhdmUtc2VjcmV0YS1zdXBlci1zZWd1cmEtcGFyYS1vLWJhbWNvLTEyMzQ1Ng==}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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
