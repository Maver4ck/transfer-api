package ru.dstreltsov.transferapi.security;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final SecretKey secretKey;

    public String generateToken(Long userId, String email) {
        final long issuedAt = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(new Date(issuedAt))
                .expiration(new Date(issuedAt + 1000 * 60 * 60)) //by default, по-правильному нужно выносить в проперти
                .signWith(secretKey)
                .compact();
    }
}
