package com.sgt.sgt_api.service;

import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContaServiceTest {
    private final UsuarioRepository usuarios=mock(UsuarioRepository.class);
    private final TokenContaRepository tokens=mock(TokenContaRepository.class);
    private final PasswordEncoder encoder=mock(PasswordEncoder.class);
    private final JavaMailSender mail=mock(JavaMailSender.class);
    private final TarefaRepository tarefas=mock(TarefaRepository.class);
    private final RegistroFrequenciaRepository frequencias=mock(RegistroFrequenciaRepository.class);
    private final PushSubscriptionRepository push=mock(PushSubscriptionRepository.class);
    private final ConfiguracaoFrequenciaRepository configuracoes=mock(ConfiguracaoFrequenciaRepository.class);

    private Usuario usuario() {
        Usuario u=new Usuario();u.setId(7L);u.setNome("Conta de teste");u.setEmail("teste@sgt.test");return u;
    }
    private ContaService service(boolean emailHabilitado) {
        return new ContaService(usuarios,tokens,encoder,mail,tarefas,frequencias,push,configuracoes,
            "https://sgt.example","noreply@sgt.example",emailHabilitado);
    }

    @Test void impedeGerarLinksQuandoSmtpNaoEstaHabilitado() {
        when(usuarios.existsByEmail("novo@sgt.test")).thenReturn(false);
        when(usuarios.findByEmail("teste@sgt.test")).thenReturn(Optional.of(usuario()));
        assertThrows(IllegalStateException.class,
            ()->service(false).solicitarAlteracaoEmail("teste@sgt.test","novo@sgt.test"));
        verifyNoInteractions(mail);
        verifyNoInteractions(tokens);
    }

    @Test void enviaLinkQuandoSmtpEstaHabilitado() {
        when(usuarios.existsByEmail("novo@sgt.test")).thenReturn(false);
        when(usuarios.findByEmail("teste@sgt.test")).thenReturn(Optional.of(usuario()));
        when(tokens.save(any())).thenAnswer(invocation->invocation.getArgument(0));
        service(true).solicitarAlteracaoEmail("teste@sgt.test","novo@sgt.test");
        verify(mail).send(any(SimpleMailMessage.class));
        verify(tokens).save(any());
    }
}