package com.sgt.sgt_api.repository;

import com.sgt.sgt_api.entity.Tarefa;
import com.sgt.sgt_api.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    @Modifying
    @Query("delete from Tarefa t where t.usuario.id = :usuarioId")
    int excluirTodasDoUsuario(@Param("usuarioId") Long usuarioId);

    List<Tarefa> findByUsuarioId(Long usuarioId);
    List<Tarefa> findByUsuarioIdAndDataEntregaBetween(Long usuarioId, LocalDate inicio, LocalDate fim);

    List<Tarefa> findByStatusInAndDataEntregaLessThanEqual(
            List<StatusTarefa> status,
            LocalDate dataLimite
    );
}