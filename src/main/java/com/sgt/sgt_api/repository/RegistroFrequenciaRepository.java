package com.sgt.sgt_api.repository;

import com.sgt.sgt_api.entity.RegistroFrequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistroFrequenciaRepository extends JpaRepository<RegistroFrequencia, Long> {
    List<RegistroFrequencia> findByUsuarioIdOrderByDataDesc(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}