package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.LoginRequestDTO;
import com.sgt.sgt_api.dto.request.RegistroRequestDTO;
import com.sgt.sgt_api.dto.request.AtualizarUsuarioRequestDTO;
import com.sgt.sgt_api.dto.response.LoginResponseDTO;
import com.sgt.sgt_api.dto.response.UsuarioResponseDTO;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.enums.StatusUsuario;
import com.sgt.sgt_api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos."));

        if (usuario.getStatus() != StatusUsuario.ATIVO) {
            throw new RuntimeException("Usuário não está ativo.");
        }

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }

        String token = jwtService.gerarToken(usuario.getId(), usuario.getEmail(), Boolean.TRUE.equals(dto.lembrarDeMim()));

        return new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTurma(),
                usuario.getAvatarUrl(),
                usuario.getCorCapa(),
                usuario.getImagemCapa()
        );
    }

    public UsuarioResponseDTO usuarioAtual(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        return new UsuarioResponseDTO(usuario);
    }

    public UsuarioResponseDTO atualizarUsuario(String email, AtualizarUsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        usuario.setNome(dto.getNome().trim());
        usuario.setTurma(dto.getTurma().trim());
        usuario.setAvatarUrl(dto.getAvatarUrl());
        usuario.setCorCapa(dto.getCorCapa() == null ? "#075b70" : dto.getCorCapa());
        usuario.setImagemCapa(dto.getImagemCapa());
        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    public void cadastrar(RegistroRequestDTO dto) {
        String emailNormalizado = dto.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new RuntimeException("E-mail já cadastrado no sistema!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(emailNormalizado);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTurma(dto.getTurma().trim());

        usuarioRepository.save(usuario);
    }
}