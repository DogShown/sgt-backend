package com.sgt.sgt_api.entity;

import com.sgt.sgt_api.enums.StatusUsuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 50)
    private String turma;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status = StatusUsuario.ATIVO;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Imagens otimizadas em data URL para persistência no SQL Server.
    @Column(name = "avatar_url", columnDefinition = "NVARCHAR(MAX)")
    private String avatarUrl;

    @Column(name = "cor_capa", length = 7)
    private String corCapa = "#075b70";

    @Column(name = "imagem_capa", columnDefinition = "NVARCHAR(MAX)")
    private String imagemCapa;

    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senha, String turma, StatusUsuario status) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.turma = turma;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }
    public StatusUsuario getStatus() { return status; }
    public void setStatus(StatusUsuario status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getCorCapa() { return corCapa; }
    public void setCorCapa(String corCapa) { this.corCapa = corCapa; }
    public String getImagemCapa() { return imagemCapa; }
    public void setImagemCapa(String imagemCapa) { this.imagemCapa = imagemCapa; }
}