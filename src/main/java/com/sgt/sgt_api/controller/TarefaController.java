package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.TarefaRequestDTO;
import com.sgt.sgt_api.dto.response.TarefaResponseDTO;
import com.sgt.sgt_api.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criar(
            @Valid @RequestBody TarefaRequestDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(tarefaService.criar(dto, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> listar(Authentication authentication) {
        return ResponseEntity.ok(tarefaService.listarPorUsuario(authentication.getName()));
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<TarefaResponseDTO> concluir(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(tarefaService.concluirTarefa(id, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        tarefaService.deletar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}