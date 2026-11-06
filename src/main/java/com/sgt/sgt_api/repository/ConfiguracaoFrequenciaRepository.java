package com.sgt.sgt_api.repository;

import com.sgt.sgt_api.entity.ConfiguracaoFrequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConfiguracaoFrequenciaRepository extends JpaRepository<ConfiguracaoFrequencia, Long> {
    Optional<ConfiguracaoFrequencia> findByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}