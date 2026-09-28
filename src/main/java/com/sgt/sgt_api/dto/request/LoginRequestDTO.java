package com.sgt.sgt_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public record LoginRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "E-mail em formato inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {
    public String getEmail() {
        return  email;
    }

    public @Nullable CharSequence getSenha() {
        return  senha;
    }
}