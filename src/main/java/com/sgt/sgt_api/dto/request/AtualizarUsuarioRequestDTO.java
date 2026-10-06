package com.sgt.sgt_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AtualizarUsuarioRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "A turma é obrigatória.")
    private String turma;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }
}
