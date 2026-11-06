package com.sgt.sgt_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TarefaRequestDTO(
        @NotBlank(message = "O título é obrigatório.")
        String titulo,

        String descricao,

        @NotBlank(message = "A categoria é obrigatória.")
        String categoria,

        @NotBlank(message = "A prioridade é obrigatória.")
        String prioridade,

        @NotNull(message = "A data de entrega é obrigatória.")
        LocalDate dataEntrega
) {}