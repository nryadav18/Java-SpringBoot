package com.course.jwtauth.security;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/*
 * JwtService - everything about tokens in ONE class: create, read, check.
 *
 * A JWT looks like   xxxxx.yyyyy.zzzzz
 *                    header.payload.signature   (each part Base64URL-encoded)
 *
 *   header    {"alg":"HS256"}
 *   payload   {"sub":"alice","roles":["ROLE_USER"],"iss":"food-delivery-api","iat":1760000000,"exp":1760000900}
 *   signature HMAC-SHA256(header + "." + payload, SECRET_KEY)
 *
 * Anyone can DECODE header and payload (they are NOT encrypted!).
 * Only someone with the SECRET KEY can produce a matching signature.
 * So: if the signature checks out, nobody has changed the payload since we signed it.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        // The secret in application.properties is Base64 text -> decode to raw bytes -> HMAC key.
        // Keys.hmacShaKeyFor throws WeakKeyException if the key is shorter than 256 bits. Good!
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
    }

    /** Creates a signed token for a user who has just proved their password. */
    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(user.getUsername())                          // "sub": who the token is about
                .claim("roles", roles)                                // custom claim (just information)
                .issuer(properties.issuer())                          // "iss": who created it
                .issuedAt(Date.from(now))                             // "iat": when
                .expiration(Date.from(now.plus(properties.expiration()))) // "exp": until when
                .signWith(signingKey)                                 // HS256, chosen from the key size
                .compact();                                           // -> "xxxxx.yyyyy.zzzzz"
    }

    /**
     * Reads the username ("sub") from a token.
     * Throws a JwtException if the token is broken, tampered with, signed with another key,
     * or EXPIRED - JJWT checks the "exp" claim for us during parsing.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** True if the token is genuine, not expired, and belongs to this user. */
    public boolean isTokenValid(String token, UserDetails user) {
        try {
            Claims claims = parseClaims(token);
            return user.getUsername().equals(claims.getSubject())
                    && properties.issuer().equals(claims.getIssuer());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long expirationSeconds() {
        return properties.expiration().toSeconds();
    }

    /*
     * The heart of validation:
     *   verifyWith(key)        -> recompute the signature and compare
     *   parseSignedClaims(..)  -> also rejects expired tokens and unsigned ("alg":"none") tokens
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
