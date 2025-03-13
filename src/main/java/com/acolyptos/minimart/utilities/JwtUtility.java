package com.acolyptos.minimart.utilities;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import java.util.Map;
import java.util.HashMap;

import javax.crypto.SecretKey;

public class JwtUtility {
  private static final Dotenv DOTENV = Dotenv.load();
  private static final String SECRET_KEY = DOTENV.get("SECRET_KEY");
  private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  public static String generateToken(String userId, String role) {
    long expirationTime = 1000 * 60 * 60; // 1 hour

    return Jwts.builder()
        .subject(userId)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(key, Jwts.SIG.HS256)
        .compact();
  }

  public static boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);

      return true;
    } catch (JwtException e) {
      System.err.println(e.getMessage());
      return false;
    }
  }

  public static Map<String, String> extraceUserIdAndRole(String token) {
    Claims claim = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    String userId = claim.getSubject();
    String role = claim.get("role", String.class);

    Map<String, String> result = new HashMap<>();

    result.put("userId", userId);
    result.put("role", role);

    return result;

  }
}
