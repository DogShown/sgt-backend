package com.sgt.sgt_api.dto.request;

import java.time.LocalDateTime;

public class MensagemChatDTO {

    private String remetente;
    private String conteudo;
    private String turma;
    private LocalDateTime dataHora = LocalDateTime.now();

    public MensagemChatDTO() {}

    public MensagemChatDTO(String remetente, String conteudo, String turma) {
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.turma = turma;
        this.dataHora = LocalDateTime.now();
    }

    public String getRemetente() { return remetente; }
    public void setRemetente(String remetente) { this.remetente = remetente; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}