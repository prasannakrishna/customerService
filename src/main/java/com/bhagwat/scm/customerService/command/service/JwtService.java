package com.bhagwat.scm.customerService.command.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * JWT + DPoP token service for customer authentication.
 *
 * DPoP (RFC 9449): Client proves possession of a key pair.
 * - Client sends a DPoP proof (signed JWT) in the DPoP header
 * - Server binds the access token to the client's public key thumbprint (jkt)
 * - On subsequent requests, server verifies the DPoP proof matches the bound key
 */
@Service
public class JwtService {

    @Value("${jwt.secret:commart-scm-customer-auth-secret-key-256bit-min}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}") // 1 hour
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms:604800000}") // 7 days
    private long refreshExpirationMs;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate access token with DPoP binding.
     * @param dpopJkt JWK Thumbprint from client's DPoP proof (null if DPoP not used)
     */
    public String generateAccessToken(UUID customerId, String username, String email, String dpopJkt) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customerId.toString());
        claims.put("username", username);
        claims.put("email", email);
        claims.put("type", "access");
        if (dpopJkt != null) {
            // DPoP-bound token: include confirmation claim
            claims.put("cnf", Map.of("jkt", dpopJkt));
        }

        return Jwts.builder()
                .claims(claims)
                .subject(customerId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(UUID customerId) {
        return Jwts.builder()
                .claim("customerId", customerId.toString())
                .claim("type", "refresh")
                .subject(customerId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getKey())
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Validate DPoP proof and extract JWK Thumbprint.
     * DPoP proof is a JWT signed by the client's private key containing:
     *   - htm: HTTP method
     *   - htu: HTTP URI
     *   - iat: issued at
     *   - jti: unique ID
     * The public key is in the JWT header's "jwk" field.
     */
    @SuppressWarnings("unchecked")
    public String validateDpopProof(String dpopProof, String httpMethod, String httpUri) {
        if (dpopProof == null || dpopProof.isBlank()) return null;

        try {
            // Decode header to get the public key (jwk)
            String[] parts = dpopProof.split("\\.");
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            Map<String, Object> header = new com.fasterxml.jackson.databind.ObjectMapper().readValue(headerJson, Map.class);

            // Verify it's a DPoP proof
            if (!"dpop+jwt".equals(header.get("typ"))) {
                throw new RuntimeException("Invalid DPoP proof: typ must be dpop+jwt");
            }

            // Extract JWK from header and compute thumbprint
            Map<String, Object> jwk = (Map<String, Object>) header.get("jwk");
            if (jwk == null) throw new RuntimeException("DPoP proof missing jwk in header");

            String jkt = computeJwkThumbprint(jwk);

            // Decode payload and verify claims
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> payload = new com.fasterxml.jackson.databind.ObjectMapper().readValue(payloadJson, Map.class);

            if (!httpMethod.equalsIgnoreCase((String) payload.get("htm"))) {
                throw new RuntimeException("DPoP htm mismatch");
            }
            if (!httpUri.equals(payload.get("htu"))) {
                throw new RuntimeException("DPoP htu mismatch");
            }

            // Check iat is within 5 minutes
            long iat = ((Number) payload.get("iat")).longValue();
            if (Math.abs(System.currentTimeMillis() / 1000 - iat) > 300) {
                throw new RuntimeException("DPoP proof expired");
            }

            return jkt;
        } catch (Exception e) {
            throw new RuntimeException("Invalid DPoP proof: " + e.getMessage());
        }
    }

    /**
     * Verify that a DPoP-bound token matches the presented DPoP proof.
     */
    public boolean verifyDpopBinding(Claims tokenClaims, String dpopJkt) {
        Object cnf = tokenClaims.get("cnf");
        if (cnf == null) return true; // Token not DPoP-bound, allow
        if (cnf instanceof Map) {
            String boundJkt = (String) ((Map<?, ?>) cnf).get("jkt");
            return dpopJkt != null && dpopJkt.equals(boundJkt);
        }
        return false;
    }

    private String computeJwkThumbprint(Map<String, Object> jwk) {
        try {
            // RFC 7638: JWK Thumbprint = SHA-256 of canonical JSON of required members
            String canonical = "{\"e\":\"" + jwk.get("e") + "\",\"kty\":\"" + jwk.get("kty") + "\",\"n\":\"" + jwk.get("n") + "\"}";
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute JWK thumbprint");
        }
    }
}
