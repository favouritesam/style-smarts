package com.stylesmart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// This annotation marks this class as a Spring Service
// It handles JWT token generation and validation
@Service
public class JwtService {

    // This annotation injects the secret key from application.properties
    // The secret key is used to sign and verify JWT tokens
    @Value("${jwt.secret:mySecretKey}")
    private String secretKey;

    // This annotation injects the token expiration time from application.properties
    // Default is 86400000 milliseconds (24 hours)
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    // This method extracts the username from a JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // This method extracts a specific claim from a JWT token
    // Claims are pieces of information stored in the token (like username, expiration, etc.)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // This method generates a JWT token for a given user
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // This method generates a JWT token with extra claims
    // Claims can include additional information like user role, permissions, etc.
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Set additional claims
                .setSubject(userDetails.getUsername()) // Set username as subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set issue date
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Set expiration date
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact(); // Build the token
    }

    // This method validates if a JWT token is valid for a given user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // This method checks if a JWT token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // This method extracts the expiration date from a JWT token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // This method extracts all claims from a JWT token
    // It parses the token using the secret key
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // This method generates the signing key from the secret key string
    // The key must be at least 256 bits for HS256 algorithm
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
