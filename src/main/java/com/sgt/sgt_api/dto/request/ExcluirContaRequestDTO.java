package com.sgt.sgt_api.dto.request;
import jakarta.validation.constraints.NotBlank;
public record ExcluirContaRequestDTO(@NotBlank String senha) {}