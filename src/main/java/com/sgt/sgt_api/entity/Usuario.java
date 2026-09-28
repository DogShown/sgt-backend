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
}