package com.sgt.sgt_api.dto.response;

import com.sgt.sgt_api.entity.ConfiguracaoFrequencia;
import java.math.BigDecimal;
import java.util.List;

public record ConfiguracaoFrequenciaResponseDTO(
    BigDecimal limite, Integer totalPrevisto, String unidade, boolean disciplinasAtivas, List<String> disciplinas
) {
    public ConfiguracaoFrequenciaResponseDTO(ConfiguracaoFrequencia c, List<String> disciplinas) {
        this(c.getLimite(), c.getTotalPrevisto(), c.getUnidade(), c.isDisciplinasAtivas(), disciplinas);
    }
}