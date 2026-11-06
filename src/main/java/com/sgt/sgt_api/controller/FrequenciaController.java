package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.RegistroFrequenciaRequestDTO;
import com.sgt.sgt_api.dto.response.RegistroFrequenciaResponseDTO;
import com.sgt.sgt_api.service.FrequenciaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/frequencia")
public class FrequenciaController {
    private final FrequenciaService service;
    public FrequenciaController(FrequenciaService service){this.service=service;}
    @GetMapping public List<RegistroFrequenciaResponseDTO> listar(Authentication auth){return service.listar(auth.getName());}
    @PostMapping public ResponseEntity<RegistroFrequenciaResponseDTO> criar(@Valid @RequestBody RegistroFrequenciaRequestDTO dto, Authentication auth){return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto,auth.getName()));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth){service.excluir(id,auth.getName());return ResponseEntity.noContent().build();}
}