package com.sgt.sgt_api.dto.response;

import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.enums.StatusUsuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String turma,
        StatusUsuario status,
        String avatarUrl,
        String corCapa,
        String imagemCapa
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTurma(),
                usuario.getStatus(),
                usuario.getAvatarUrl(),
                usuario.getCorCapa(),
                usuario.getImagemCapa()
        );
    }
}