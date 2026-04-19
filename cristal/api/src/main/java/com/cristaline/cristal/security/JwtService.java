package com.cristaline.cristal.security;

import com.cristaline.cristal.model.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class JwtService {

    private final String SECRET = "secret-key";

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractUsername(String token) {

        return Jwts.builder()
                .signWith(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}