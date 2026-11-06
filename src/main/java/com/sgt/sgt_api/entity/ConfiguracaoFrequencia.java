package com.sgt.sgt_api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_configuracao_frequencia", uniqueConstraints = @UniqueConstraint(name = "UQ_config_frequencia_usuario", columnNames = "usuario_id"))
public class ConfiguracaoFrequencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal limite = new BigDecimal("25.00");
    @Column(name = "total_previsto", nullable = false)
    private Integer totalPrevisto = 200;
    @Column(nullable = false, length = 10)
    private String unidade = "dias";
    @Column(name = "disciplinas_ativas", nullable = false)
    private boolean disciplinasAtivas;
    @Column(name = "disciplinas_json", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String disciplinasJson = "[]";
    public Long getId(){return id;}
    public Usuario getUsuario(){return usuario;} public void setUsuario(Usuario v){usuario=v;}
    public BigDecimal getLimite(){return limite;} public void setLimite(BigDecimal v){limite=v;}
    public Integer getTotalPrevisto(){return totalPrevisto;} public void setTotalPrevisto(Integer v){totalPrevisto=v;}
    public String getUnidade(){return unidade;} public void setUnidade(String v){unidade=v;}
    public boolean isDisciplinasAtivas(){return disciplinasAtivas;} public void setDisciplinasAtivas(boolean v){disciplinasAtivas=v;}
    public String getDisciplinasJson(){return disciplinasJson;} public void setDisciplinasJson(String v){disciplinasJson=v;}
}