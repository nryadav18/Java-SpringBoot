package com.course.jwtauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.course.jwtauth.security.JwtProperties;
import com.course.jwtauth.security.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;

/*
 * JwtServiceTest - plain unit test, NO Spring context: fast and focused.
 * We build JwtService by hand with test settings.
 */
class JwtServiceTest {

    private static final String SECRET = "VPRUfjavGU03Le4EyEe/zdh5sHYm5ZtiWXxqhb3pf0o=";
    private static final String OTHER_SECRET = "c2VjcmV0LWtleS1mb3ItYW5vdGhlci1hcHAtMzJieXRlcyE=";

    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, Duration.ofMinutes(15), "test"));
    private final UserDetails alice = User.withUsername("alice").password("x").roles("USER").build();

    @Test
    void tokenHasThreePartsAndCarriesTheUsername() {
        String token = jwtService.generateToken(alice);

        assertThat(token.split("\\.")).hasSize(3);                 // header.payload.signature
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtService.isTokenValid(token, alice)).isTrue();
    }

    @Test
    void tokenOfAnotherUserIsNotValidForAlice() {
        UserDetails bob = User.withUsername("bob").password("x").roles("USER").build();
        String bobsToken = jwtService.generateToken(bob);

        assertThat(jwtService.isTokenValid(bobsToken, alice)).isFalse();
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtService.generateToken(alice);
        // change one character in the signature part
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1) + (last == 'A' ? 'B' : 'A');

        assertThatThrownBy(() -> jwtService.extractUsername(tampered)).isInstanceOf(JwtException.class);
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        JwtService otherApp = new JwtService(new JwtProperties(OTHER_SECRET, Duration.ofMinutes(15), "test"));
        String foreignToken = otherApp.generateToken(alice);

        assertThatThrownBy(() -> jwtService.extractUsername(foreignToken)).isInstanceOf(SignatureException.class);
    }

    @Test
    void expiredTokenIsRejected() {
        // expiration in the PAST -> already expired when created
        JwtService shortLived = new JwtService(new JwtProperties(SECRET, Duration.ofSeconds(-5), "test"));
        String expired = shortLived.generateToken(alice);

        assertThatThrownBy(() -> jwtService.extractUsername(expired)).isInstanceOf(ExpiredJwtException.class);
        assertThat(jwtService.isTokenValid(expired, alice)).isFalse();
    }
}
