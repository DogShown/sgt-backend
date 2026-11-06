package com.sgt.sgt_api.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ConfiguracaoFrequenciaRequestDTO(
    @NotNull @DecimalMin("1.0") @DecimalMax("100.0") BigDecimal limite,
    @NotNull @Min(1) @Max(100000) Integer totalPrevisto,
    @NotBlank @Pattern(regexp="dias|aulas|horas") String unidade,
    boolean disciplinasAtivas,
    @Size(max=100) List<@NotBlank @Size(max=100) String> disciplinas
) {}