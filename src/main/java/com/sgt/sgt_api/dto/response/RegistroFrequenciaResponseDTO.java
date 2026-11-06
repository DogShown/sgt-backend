package com.sgt.sgt_api.dto.response;

import com.sgt.sgt_api.entity.RegistroFrequencia;
import java.time.LocalDate;

public record RegistroFrequenciaResponseDTO(Long id, LocalDate data, Integer quantidade, String materia, boolean justificada, String unidade) {
    public RegistroFrequenciaResponseDTO(RegistroFrequencia r) {
        this(r.getId(), r.getData(), r.getQuantidade(), r.getMateria(), r.isJustificada(), r.getUnidade());
    }
}