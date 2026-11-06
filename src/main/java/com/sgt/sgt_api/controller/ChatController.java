package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.MensagemChatDTO;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.UsuarioRepository;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class ChatController {
    private final UsuarioRepository usuarios;
    public ChatController(UsuarioRepository usuarios){this.usuarios=usuarios;}

    @MessageMapping("/chat.enviar/{turma}")
    @SendTo("/topic/turma/{turma}")
    public MensagemChatDTO enviarMensagem(@DestinationVariable String turma, MensagemChatDTO mensagem, Principal principal) {
        Usuario usuario=usuarios.findByEmail(principal.getName()).orElseThrow(()->new IllegalArgumentException("Usuário não encontrado."));
        if(!turma.equalsIgnoreCase(usuario.getTurma())) throw new IllegalArgumentException("Acesso permitido somente à sua turma.");
        String conteudo=mensagem.getConteudo()==null?"":mensagem.getConteudo().trim();
        if(conteudo.isBlank() || conteudo.length()>2000) throw new IllegalArgumentException("A mensagem deve ter entre 1 e 2000 caracteres.");
        return new MensagemChatDTO(usuario.getNome(),conteudo,usuario.getTurma());
    }
}