package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.*;
import com.sgt.sgt_api.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/conta")
public class ContaController {
 private final ContaService service;
 public ContaController(ContaService service){this.service=service;}
 @PostMapping("/recuperar-senha") public ResponseEntity<Void> recuperar(@Valid @RequestBody EmailRequestDTO dto){service.solicitarRedefinicao(dto.email());return ResponseEntity.accepted().build();}
 @PostMapping("/redefinir-senha") public ResponseEntity<Void> redefinir(@Valid @RequestBody RedefinirSenhaRequestDTO dto){service.redefinirSenha(dto);return ResponseEntity.noContent().build();}
 @PostMapping("/alterar-email") public ResponseEntity<Void> alterarEmail(@Valid @RequestBody EmailRequestDTO dto,Authentication auth){service.solicitarAlteracaoEmail(auth.getName(),dto.email());return ResponseEntity.accepted().build();}
 @PostMapping("/confirmar-email") public ResponseEntity<Void> confirmarEmail(@Valid @RequestBody ConfirmarTokenRequestDTO dto){service.confirmarEmail(dto.token());return ResponseEntity.noContent().build();}
 @DeleteMapping public ResponseEntity<Void> excluir(@Valid @RequestBody ExcluirContaRequestDTO dto,Authentication auth){service.excluirConta(auth.getName(),dto.senha());return ResponseEntity.noContent().build();}
}