package com.sgt.sgt_api.config;

import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.UsuarioRepository;
import com.sgt.sgt_api.service.JwtService;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.util.List;

@Component
public class ChatAuthenticationInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final UsuarioRepository usuarios;
    public ChatAuthenticationInterceptor(JwtService jwtService, UsuarioRepository usuarios){this.jwtService=jwtService;this.usuarios=usuarios;}

    @Override public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor=MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);
        if(accessor==null) return message;
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String header=accessor.getFirstNativeHeader("Authorization");
            if(header==null || !header.startsWith("Bearer ")) throw new MessageDeliveryException(message,"Autenticação obrigatória para o chat.");
            String email=jwtService.validarToken(header.substring(7)).getSubject();
            Usuario usuario=usuarios.findByEmail(email).orElseThrow(()->new MessageDeliveryException(message,"Conta não encontrada."));
            accessor.setUser(new UsernamePasswordAuthenticationToken(usuario.getEmail(),null,List.of()));
        }
        if(StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
            Principal principal=accessor.getUser();
            if(principal==null) throw new MessageDeliveryException(message,"Sessão do chat não autenticada.");
            Usuario usuario=usuarios.findByEmail(principal.getName()).orElseThrow(()->new MessageDeliveryException(message,"Conta não encontrada."));
            String destination=accessor.getDestination();
            String prefix=StompCommand.SEND.equals(accessor.getCommand())?"/app/chat.enviar/":"/topic/turma/";
            if(destination==null || !destination.startsWith(prefix)) throw new MessageDeliveryException(message,"Destino de chat inválido.");
            String turma=destination.substring(prefix.length()).trim();
            if(turma.isEmpty() || !turma.equalsIgnoreCase(usuario.getTurma())) throw new MessageDeliveryException(message,"Acesso permitido somente à sua turma.");
        }
        return message;
    }
}