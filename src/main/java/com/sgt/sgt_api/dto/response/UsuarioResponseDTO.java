package com.sgt.sgt_api.dto.response;

import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.enums.StatusUsuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String turma,
        StatusUsuario status
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTurma(), usuario.getStatus());
    }
}