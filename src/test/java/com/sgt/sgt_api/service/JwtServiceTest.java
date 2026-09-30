package com.sgt.sgt_api.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "sgt-test-secret-2026-09-30-with-more-than-32-characters";

    @Test
    void deveGerarEValidarToken() {
        JwtService jwtService = new JwtService(SECRET, 8);

        String token = jwtService.gerarToken(1L, "aluno@sgt.com");

        assertNotNull(token);
        assertFalse(token.isBlank());

        DecodedJWT jwt = jwtService.validarToken(token);

        assertEquals("aluno@sgt.com", jwt.getSubject());
        assertEquals(1L, jwt.getClaim("usuarioId").asLong());
        assertNotNull(jwt.getIssuedAt());
        assertNotNull(jwt.getExpiresAt());
        assertTrue(jwt.getExpiresAt().after(jwt.getIssuedAt()));
    }

    @Test
    void deveRecusarSegredoComMenosDe32Caracteres() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService("segredo-curto", 8)
        );

        assertEquals(
                "A chave JWT deve possuir pelo menos 32 caracteres.",
                exception.getMessage()
        );
    }

    @Test
    void deveRecusarTokenAlterado() {
        JwtService jwtService = new JwtService(SECRET, 8);
        String token = jwtService.gerarToken(1L, "aluno@sgt.com");

        String tokenAlterado = token.substring(0, token.length() - 1) + "x";

        assertThrows(
                JWTVerificationException.class,
                () -> jwtService.validarToken(tokenAlterado)
        );
    }
}
