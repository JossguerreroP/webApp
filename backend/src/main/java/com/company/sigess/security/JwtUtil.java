package com.company.sigess.security;

import com.company.sigess.models.DTO.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.Properties;

public class JwtUtil {
    private static String SECRET;
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    static {
        try (InputStream input = JwtUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                SECRET = "default_secret_key_should_be_long_enough_for_sha256_algorithm";
            } else {
                prop.load(input);
                SECRET = prop.getProperty("app.jwt.secret");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SECRET = "default_secret_key_should_be_long_enough_for_sha256_algorithm";
        }
    }

    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public static String generateToken(UserDTO user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.id()))
                .claim("role", user.role())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getRoleFromToken(String token) {
        return validateToken(token).get("role", String.class);
    }

    public static Long getUserIdFromToken(String token) {
        return Long.parseLong(validateToken(token).getSubject());
    }
}
