package com.course.shopeasy.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.course.shopeasy.user.Role;

/**
 * Unit test for JwtService - plain Java, we build the object ourselves with "new" (no Spring needed).
 */
class JwtServiceTest {

    // Base64 of a 46-byte text = more than the 32 bytes (256 bits) HS256 requires.
    private static final String SECRET = "c2hvcGVhc3ktZGV2LW9ubHktc2VjcmV0LWtleS1jaGFuZ2UtbWUtaW4tcHJvZA==";
    private static final String OTHER_SECRET = "YW5vdGhlci1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLWhzMjU2";

    private final UserPrincipal user = new UserPrincipal(2L, "customer@shopeasy.com", "hash", Role.CUSTOMER, true);

    @Test
    void generatedTokenIsValidAndCarriesTheEmail() {
        JwtService jwt = new JwtService(new JwtProperties(SECRET, Duration.ofMinutes(5), "shopeasy"));

        String token = jwt.generateToken(user);

        assertThat(token.split("\\.")).hasSize(3);                       // header.payload.signature
        assertThat(jwt.extractValidSubject(token)).contains("customer@shopeasy.com");
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        JwtService attacker = new JwtService(new JwtProperties(OTHER_SECRET, Duration.ofMinutes(5), "shopeasy"));
        JwtService ours = new JwtService(new JwtProperties(SECRET, Duration.ofMinutes(5), "shopeasy"));

        String forged = attacker.generateToken(user);

        assertThat(ours.extractValidSubject(forged)).isEmpty();
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwt = new JwtService(new JwtProperties(SECRET, Duration.ofSeconds(-120), "shopeasy"));

        String expired = jwt.generateToken(user);   // "expired 2 minutes ago" (beyond any clock skew)

        assertThat(jwt.extractValidSubject(expired)).isEmpty();
    }

    @Test
    void garbageIsRejected() {
        JwtService jwt = new JwtService(new JwtProperties(SECRET, Duration.ofMinutes(5), "shopeasy"));

        assertThat(jwt.extractValidSubject("not-a-jwt")).isEmpty();
        assertThat(jwt.extractValidSubject("")).isEmpty();
    }
}
