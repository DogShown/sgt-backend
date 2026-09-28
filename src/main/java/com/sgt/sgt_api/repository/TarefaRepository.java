package com.sgt.sgt_api.repository;

import com.sgt.sgt_api.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByUsuarioId(Long usuarioId);
    List<Tarefa> findByUsuarioIdAndDataEntregaBetween(Long usuarioId, LocalDate inicio, LocalDate fim);
}