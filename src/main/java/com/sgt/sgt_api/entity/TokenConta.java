package com.sgt.sgt_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="tb_token_conta", indexes=@Index(name="ix_token_hash",columnList="token_hash",unique=true))
public class TokenConta {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false) private Usuario usuario;
 @Column(name="token_hash",nullable=false,length=64,unique=true) private String tokenHash;
 @Column(nullable=false,length=30) private String finalidade;
 @Column(nullable=false) private LocalDateTime expiraEm;
 private LocalDateTime consumidoEm;
 @Column(length=100) private String emailDestino;
 public Long getId(){return id;} public Usuario getUsuario(){return usuario;} public void setUsuario(Usuario v){usuario=v;}
 public String getTokenHash(){return tokenHash;} public void setTokenHash(String v){tokenHash=v;}
 public String getFinalidade(){return finalidade;} public void setFinalidade(String v){finalidade=v;}
 public LocalDateTime getExpiraEm(){return expiraEm;} public void setExpiraEm(LocalDateTime v){expiraEm=v;}
 public LocalDateTime getConsumidoEm(){return consumidoEm;} public void setConsumidoEm(LocalDateTime v){consumidoEm=v;}
 public String getEmailDestino(){return emailDestino;} public void setEmailDestino(String v){emailDestino=v;}
}