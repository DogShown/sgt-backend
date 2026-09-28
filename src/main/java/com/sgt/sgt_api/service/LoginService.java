package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.LoginRequestDTO;
import com.sgt.sgt_api.dto.response.LoginResponseDTO;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;

    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos."));

        if (!usuario.getSenha().equals(dto.senha())) {
            throw new RuntimeException("Usuário ou senha inválidos.");
        }

        String token = UUID.randomUUID().toString();
        return new LoginResponseDTO(token, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTurma());
    }
}