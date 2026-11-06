package com.sgt.sgt_api.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record RedefinirSenhaRequestDTO(@NotBlank String token,@NotBlank @Size(min=8,max=72) String novaSenha) {}