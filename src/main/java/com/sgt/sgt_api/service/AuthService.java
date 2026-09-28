package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.LoginRequestDTO;
import com.sgt.sgt_api.dto.request.RegistroRequestDTO;
import com.sgt.sgt_api.dto.response.LoginResponseDTO;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }

        // Retorna o DTO com dados do usuário autenticado
        return new LoginResponseDTO(
                "token-jwt-exemplo",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTurma()
        );
    }

    public void cadastrar(RegistroRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado no sistema!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTurma(dto.getTurma());

        usuarioRepository.save(usuario);
    }
}