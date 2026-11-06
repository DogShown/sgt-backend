package com.sgt.sgt_api.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_registro_frequencia")
public class RegistroFrequencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @Column(nullable = false) private LocalDate data;
    @Column(nullable = false) private Integer quantidade;
    @Column(length = 100) private String materia;
    @Column(nullable = false) private boolean justificada;
    @Column(nullable = false, length = 10) private String unidade = "dias";
    public Long getId(){return id;} public Usuario getUsuario(){return usuario;} public void setUsuario(Usuario v){usuario=v;}
    public LocalDate getData(){return data;} public void setData(LocalDate v){data=v;}
    public Integer getQuantidade(){return quantidade;} public void setQuantidade(Integer v){quantidade=v;}
    public String getMateria(){return materia;} public void setMateria(String v){materia=v;}
    public boolean isJustificada(){return justificada;} public void setJustificada(boolean v){justificada=v;}
    public String getUnidade(){return unidade;} public void setUnidade(String v){unidade=v;}
}