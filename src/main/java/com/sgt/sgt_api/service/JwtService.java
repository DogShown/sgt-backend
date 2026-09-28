package com.sgt.sgt_api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expirationHours;

    public JwtService(
            @Value("${sgt.jwt.secret}") String secret,
            @Value("${sgt.jwt.expiration-hours:8}") long expirationHours
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("A chave JWT deve possuir pelo menos 32 caracteres.");
        }

        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationHours = expirationHours;
    }

    public String gerarToken(Long usuarioId, String email) {
        Instant agora = Instant.now();

        return JWT.create()
                .withSubject(email)
                .withClaim("usuarioId", usuarioId)
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(agora.plus(expirationHours, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    public DecodedJWT validarToken(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }
}
