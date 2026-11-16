package org.eventmanagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.eventmanagement.entity.Admin;
import org.eventmanagement.entity.Organizer;
import org.eventmanagement.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 15 * 60 * 1000);
        return Jwts.builder().subject(user.getUserEmail()).claim("role", "USER").issuedAt(now).expiration(expiration).signWith(secretKey).compact();
    }
    public Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
    public String generateAdminToken(Admin admin){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 15 * 60 * 1000);
        return Jwts.builder().subject(admin.getAdminEmail()).claim("role" , "ADMIN").issuedAt(now).expiration(expiration).signWith(secretKey).compact();
    }
    public String generateOrganizerToken(Organizer organizer) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 15 * 60 * 1000);

        return Jwts.builder()
                .subject(organizer.getOrganizerEmail())
                .claim("role", "ORGANIZER")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}