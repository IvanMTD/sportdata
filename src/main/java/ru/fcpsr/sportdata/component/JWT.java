package ru.fcpsr.sportdata.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWT {
    private final long accessExpiration;
    private final long refreshExpiration;
    private final SecretKey key;

    public JWT(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration.access}") long accessExpiration, @Value("${jwt.expiration.refresh}") long refreshExpiration){
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, String digitalSignature){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .claim("digital_signature",digitalSignature)
                .compact();
    }

    public String generateRefreshToken(String username, String digitalSignature) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .claim("digital_signature",digitalSignature)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public String getDigitalSignatureFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            if (claims.containsKey("digital_signature")) {
                return (String) claims.get("digital_signature");
            } else {
                throw new IllegalArgumentException("Token does not contain digital_signature claim");
            }
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public long getAccessExpiration() {
        return accessExpiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}
