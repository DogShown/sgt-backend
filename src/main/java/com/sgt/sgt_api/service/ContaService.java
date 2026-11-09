package com.sgt.sgt_api.service;

import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.PushSubscriptionRepository;
import com.sgt.sgt_api.repository.TarefaRepository;
import com.sgt.sgt_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaService {

    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;

    public ContaService(
            UsuarioRepository usuarioRepository,
            TarefaRepository tarefaRepository,
            PushSubscriptionRepository pushSubscriptionRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tarefaRepository = tarefaRepository;
        this.pushSubscriptionRepository = pushSubscriptionRepository;
    }

    @Transactional
    public void excluirConta(String emailAutenticado) {
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "A conta autenticada não foi encontrada."
                ));

        Long usuarioId = usuario.getId();

        // As FKs do banco usam ON DELETE NO ACTION:
        // remova primeiro os registros dependentes do próprio usuário.
        tarefaRepository.excluirTodasDoUsuario(usuarioId);
        pushSubscriptionRepository.excluirTodasDoUsuario(usuarioId);

        // A exclusão só é confirmada se todas as etapas forem concluídas.
        usuarioRepository.delete(usuario);
    }
}
