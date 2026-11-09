package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conta")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @DeleteMapping
    public ResponseEntity<Void> excluirConta(Authentication authentication) {
        contaService.excluirConta(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
