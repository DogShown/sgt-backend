package com.sgt.sgt_api.dto.response;

import com.sgt.sgt_api.entity.Tarefa;
import com.sgt.sgt_api.enums.StatusTarefa;
import java.time.LocalDate;

public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        String prioridade,
        LocalDate dataEntrega,
        StatusTarefa status,
        Long usuarioId
) {
    public TarefaResponseDTO(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getCategoria(),
                tarefa.getPrioridade(),
                tarefa.getDataEntrega(),
                tarefa.getStatus(),
                tarefa.getUsuario().getId()
        );
    }
}