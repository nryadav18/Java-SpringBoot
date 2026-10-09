package com.course.shopeasy.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Creates and checks JSON Web Tokens with JJWT 0.12.6 (topic 49).
 *
 * A token looks like  xxxxx.yyyyy.zzzzz  = header . payload (claims) . signature.
 * Our payload:
 * <pre>
 * { "iss": "shopeasy", "sub": "customer@shopeasy.com", "uid": 2, "role": "CUSTOMER",
 *   "iat": 1760000000, "exp": 1760003600 }
 * </pre>
 * The signature (HMAC-SHA256 with our secret key) proves WE issued the token and nobody changed it.
 * Remember: the payload is only Base64-encoded, NOT encrypted - never put secrets in it.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey signingKey;
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        // Keys.hmacShaKeyFor throws if the key is shorter than 256 bits - a weak key fails at startup.
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
    }

    /** Builds a signed token for a user who just logged in or registered. */
    public String generateToken(UserPrincipal user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(user.email())
                .claim("uid", user.id())
                .claim("role", user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.expiration())))
                .signWith(signingKey)               // algorithm (HS256/384/512) is chosen from the key size
                .compact();
    }

    /**
     * Returns the e-mail (subject) if the token is valid: correct signature, our issuer, not expired.
     * Returns empty for ANY problem - the caller simply treats the request as anonymous.
     */
    public Optional<String> extractValidSubject(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.issuer())
                    .build()
                    .parseSignedClaims(token)       // throws if signature is wrong or token expired
                    .getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException ex) {
            // DEBUG, not WARN: expired tokens are normal and would flood the logs.
            log.debug("Rejected JWT: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public Duration getExpiration() {
        return properties.expiration();
    }
}
