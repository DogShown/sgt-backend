package com.sgt.sgt_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AtualizarUsuarioRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter até 100 caracteres.")
    private String nome;

    @NotBlank(message = "A turma é obrigatória.")
    @Size(max = 50, message = "A turma deve ter até 50 caracteres.")
    private String turma;

    @Size(max = 3000000, message = "A foto de perfil excede o tamanho permitido.")
    private String avatarUrl;

    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "A cor de fundo deve estar no formato hexadecimal.")
    private String corCapa;

    @Size(max = 3000000, message = "A imagem de capa excede o tamanho permitido.")
    private String imagemCapa;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCorCapa() { return corCapa; }
    public void setCorCapa(String corCapa) { this.corCapa = corCapa; }

    public String getImagemCapa() { return imagemCapa; }
    public void setImagemCapa(String imagemCapa) { this.imagemCapa = imagemCapa; }
}