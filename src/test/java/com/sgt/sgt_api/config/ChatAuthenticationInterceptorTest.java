package com.sgt.sgt_api.config;

import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.UsuarioRepository;
import com.sgt.sgt_api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import java.security.Principal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatAuthenticationInterceptorTest {
    @Mock JwtService jwtService;
    @Mock UsuarioRepository usuarios;
    @InjectMocks ChatAuthenticationInterceptor interceptor;

    private Usuario usuario(String email,String turma) {
        Usuario u=new Usuario();u.setEmail(email);u.setTurma(turma);return u;
    }
    private Message<byte[]> mensagem(StompCommand comando,String destino,Principal principal) {
        StompHeaderAccessor accessor=StompHeaderAccessor.create(comando);
        accessor.setDestination(destino);accessor.setUser(principal);accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0],accessor.getMessageHeaders());
    }

    @Test void permiteInscricaoNaTurmaDoUsuario() {
        when(usuarios.findByEmail("aluna@sgt.test")).thenReturn(Optional.of(usuario("aluna@sgt.test","INF3GM")));
        Message<byte[]> message=mensagem(StompCommand.SUBSCRIBE,"/topic/turma/INF3GM",()->"aluna@sgt.test");
        assertDoesNotThrow(()->interceptor.preSend(message,null));
    }

    @Test void bloqueiaInscricaoEmOutraTurma() {
        when(usuarios.findByEmail("aluna@sgt.test")).thenReturn(Optional.of(usuario("aluna@sgt.test","INF3GM")));
        Message<byte[]> message=mensagem(StompCommand.SUBSCRIBE,"/topic/turma/INF3EM",()->"aluna@sgt.test");
        assertThrows(MessageDeliveryException.class,()->interceptor.preSend(message,null));
    }

    @Test void permiteDuasContasDaMesmaTurmaNoMesmoCanal() {
        when(usuarios.findByEmail("aluna1@sgt.test")).thenReturn(Optional.of(usuario("aluna1@sgt.test","INF3GM")));
        when(usuarios.findByEmail("aluno2@sgt.test")).thenReturn(Optional.of(usuario("aluno2@sgt.test","INF3GM")));
        Message<byte[]> primeira=mensagem(StompCommand.SUBSCRIBE,"/topic/turma/INF3GM",()->"aluna1@sgt.test");
        Message<byte[]> segunda=mensagem(StompCommand.SUBSCRIBE,"/topic/turma/INF3GM",()->"aluno2@sgt.test");
        assertDoesNotThrow(()->interceptor.preSend(primeira,null));
        assertDoesNotThrow(()->interceptor.preSend(segunda,null));
    }

    @Test void impedeContaDeOutraTurmaDeEntrarNoCanal() {
        when(usuarios.findByEmail("aluno2@sgt.test")).thenReturn(Optional.of(usuario("aluno2@sgt.test","INF3EM")));
        Message<byte[]> message=mensagem(StompCommand.SUBSCRIBE,"/topic/turma/INF3GM",()->"aluno2@sgt.test");
        assertThrows(MessageDeliveryException.class,()->interceptor.preSend(message,null));
    }

    @Test void bloqueiaEnvioSemSessaoAutenticada() {
        Message<byte[]> message=mensagem(StompCommand.SEND,"/app/chat.enviar/INF3GM",null);
        assertThrows(MessageDeliveryException.class,()->interceptor.preSend(message,null));
    }
}