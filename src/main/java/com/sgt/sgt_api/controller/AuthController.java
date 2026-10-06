package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.LoginRequestDTO;
import com.sgt.sgt_api.dto.request.RegistroRequestDTO;
import com.sgt.sgt_api.dto.request.AtualizarUsuarioRequestDTO;
import com.sgt.sgt_api.dto.response.LoginResponseDTO;
import com.sgt.sgt_api.dto.response.UsuarioResponseDTO;
import com.sgt.sgt_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody RegistroRequestDTO dto) {
        authService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(Authentication authentication, @Valid @RequestBody AtualizarUsuarioRequestDTO dto) {
        return ResponseEntity.ok(authService.atualizarUsuario(authentication.getName(), dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> usuarioAtual(Authentication authentication) {
        return ResponseEntity.ok(authService.usuarioAtual(authentication.getName()));
    }
}
