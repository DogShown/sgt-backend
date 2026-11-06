package com.sgt.sgt_api.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegistroFrequenciaRequestDTO(
    @NotNull LocalDate data,
    @NotNull @Min(1) @Max(100) Integer quantidade,
    @Size(max=100) String materia,
    boolean justificada,
    @NotBlank @Pattern(regexp="dias|aulas|horas") String unidade
) {}