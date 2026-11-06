package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.TarefaRequestDTO;
import com.sgt.sgt_api.dto.response.TarefaResponseDTO;
import com.sgt.sgt_api.service.TarefaService;
import com.sgt.sgt_api.enums.StatusTarefa;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/tarefas")
public class TarefaController {
    private final TarefaService tarefaService;
    public TarefaController(TarefaService tarefaService){this.tarefaService=tarefaService;}
    @PostMapping public ResponseEntity<TarefaResponseDTO> criar(@Valid @RequestBody TarefaRequestDTO dto,Authentication auth){return ResponseEntity.ok(tarefaService.criar(dto,auth.getName()));}
    @GetMapping public ResponseEntity<List<TarefaResponseDTO>> listar(Authentication auth){return ResponseEntity.ok(tarefaService.listarPorUsuario(auth.getName()));}
    @PutMapping("/{id}") public ResponseEntity<TarefaResponseDTO> atualizar(@PathVariable Long id,@Valid @RequestBody TarefaRequestDTO dto,Authentication auth){return ResponseEntity.ok(tarefaService.atualizar(id,auth.getName(),dto));}
    @PutMapping("/{id}/status") public ResponseEntity<TarefaResponseDTO> atualizarStatus(@PathVariable Long id,@RequestParam StatusTarefa status,Authentication auth){return ResponseEntity.ok(tarefaService.atualizarStatus(id,auth.getName(),status));}
    @PutMapping("/{id}/concluir") public ResponseEntity<TarefaResponseDTO> concluir(@PathVariable Long id,Authentication auth){return ResponseEntity.ok(tarefaService.concluirTarefa(id,auth.getName()));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> deletar(@PathVariable Long id,Authentication auth){tarefaService.deletar(id,auth.getName());return ResponseEntity.noContent().build();}
}