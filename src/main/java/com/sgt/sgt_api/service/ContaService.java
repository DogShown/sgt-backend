package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.*;
import com.sgt.sgt_api.entity.*;
import com.sgt.sgt_api.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service @Transactional
public class ContaService {
 private static final String RESET="RESET_SENHA", EMAIL="ALTERAR_EMAIL";
 private final UsuarioRepository usuarios; private final TokenContaRepository tokens; private final PasswordEncoder encoder;
 private final JavaMailSender mail; private final TarefaRepository tarefas; private final RegistroFrequenciaRepository frequencias;
 private final PushSubscriptionRepository push; private final String frontendUrl; private final String remetente;
 public ContaService(UsuarioRepository usuarios,TokenContaRepository tokens,PasswordEncoder encoder,JavaMailSender mail,TarefaRepository tarefas,RegistroFrequenciaRepository frequencias,PushSubscriptionRepository push,
  @Value("${sgt.frontend.url:http://localhost:5173}") String frontendUrl,@Value("${sgt.mail.from:noreply@sgt.local}") String remetente){
  this.usuarios=usuarios;this.tokens=tokens;this.encoder=encoder;this.mail=mail;this.tarefas=tarefas;this.frequencias=frequencias;this.push=push;this.frontendUrl=frontendUrl;this.remetente=remetente;
 }
 public void solicitarRedefinicao(String email){
  usuarios.findByEmail(email.trim().toLowerCase()).ifPresent(u->emitirToken(u,RESET,u.getEmail(),"redefinir-senha","Redefinição de senha"));
 }
 public void redefinirSenha(RedefinirSenhaRequestDTO dto){
  TokenConta t=validar(dto.token(),RESET);t.getUsuario().setSenha(encoder.encode(dto.novaSenha()));usuarios.save(t.getUsuario());consumir(t);
 }
 public void solicitarAlteracaoEmail(String emailAtual,String novoEmail){
  String destino=novoEmail.trim().toLowerCase();
  if(usuarios.existsByEmail(destino)) throw new IllegalArgumentException("Este e-mail já está cadastrado.");
  Usuario u=usuarios.findByEmail(emailAtual).orElseThrow(()->new IllegalArgumentException("Conta não encontrada."));
  emitirToken(u,EMAIL,destino,"confirmar-email","Confirme seu novo e-mail");
 }
 public void confirmarEmail(String raw){
  TokenConta t=validar(raw,EMAIL);String destino=t.getEmailDestino();
  if(usuarios.existsByEmail(destino)) throw new IllegalArgumentException("Este e-mail já está cadastrado.");
  t.getUsuario().setEmail(destino);usuarios.save(t.getUsuario());consumir(t);
 }
 public void excluirConta(String email,String senha){
  Usuario u=usuarios.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Conta não encontrada."));
  if(!encoder.matches(senha,u.getSenha())) throw new IllegalArgumentException("Senha incorreta.");
  Long id=u.getId();push.deleteByUsuarioId(id);tarefas.deleteByUsuarioId(id);frequencias.deleteByUsuarioId(id);tokens.deleteByUsuarioId(id);usuarios.delete(u);
 }
 private void emitirToken(Usuario u,String finalidade,String destino,String rota,String assunto){
  tokens.deleteByUsuarioIdAndFinalidadeAndConsumidoEmIsNull(u.getId(),finalidade);
  String raw=UUID.randomUUID().toString().replace("-","")+UUID.randomUUID().toString().replace("-","");
  TokenConta t=new TokenConta();t.setUsuario(u);t.setFinalidade(finalidade);t.setTokenHash(hash(raw));t.setEmailDestino(destino);t.setExpiraEm(LocalDateTime.now().plusMinutes(30));tokens.save(t);
  SimpleMailMessage msg=new SimpleMailMessage();msg.setFrom(remetente);msg.setTo(destino);msg.setSubject("SGT - "+assunto);
  msg.setText("Olá, "+u.getNome()+".\n\nAcesse o link abaixo em até 30 minutos para concluir a solicitação:\n"+frontendUrl+"/"+rota+"?token="+raw+"\n\nSe você não solicitou esta alteração, ignore esta mensagem.");
  try{mail.send(msg);}catch(MailException e){throw new IllegalStateException("Não foi possível enviar o e-mail. O serviço de e-mail precisa ser configurado.");}
 }
 private TokenConta validar(String raw,String finalidade){
  if(raw==null || raw.isBlank()) throw new IllegalArgumentException("Token inválido ou expirado.");
  TokenConta t=tokens.findByTokenHashAndFinalidadeAndConsumidoEmIsNull(hash(raw),finalidade).orElseThrow(()->new IllegalArgumentException("Token inválido ou expirado."));
  if(t.getExpiraEm().isBefore(LocalDateTime.now())){tokens.delete(t);throw new IllegalArgumentException("Token inválido ou expirado.");}
  return t;
 }
 private void consumir(TokenConta t){t.setConsumidoEm(LocalDateTime.now());tokens.save(t);}
 private String hash(String raw){
  try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8)));}
  catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
 }
}