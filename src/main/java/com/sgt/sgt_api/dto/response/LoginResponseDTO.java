package com.sgt.sgt_api.dto.response;

public record LoginResponseDTO(
        String token,
        Long id,
        String nome,
        String email,
        String turma
) {}