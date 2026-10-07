package com.sgt.sgt_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgt.sgt_api.config.PushConfig.PushSettings;
import com.sgt.sgt_api.dto.request.PushSubscriptionRequestDTO;
import com.sgt.sgt_api.entity.PushSubscription;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.entity.Tarefa;
import com.sgt.sgt_api.enums.StatusTarefa;
import com.sgt.sgt_api.repository.TarefaRepository;
import com.sgt.sgt_api.repository.PushSubscriptionRepository;
import com.sgt.sgt_api.repository.UsuarioRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PushNotificationService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;
    private final PushSettings settings;
    private final ObjectMapper objectMapper;

    public PushNotificationService(
            PushSubscriptionRepository subscriptionRepository,
            UsuarioRepository usuarioRepository,
            TarefaRepository tarefaRepository,
            PushSettings settings,
            ObjectMapper objectMapper
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.usuarioRepository = usuarioRepository;
        this.tarefaRepository = tarefaRepository;
        this.settings = settings;
        this.objectMapper = objectMapper;
    }

    public String getPublicKey() {
        if (!settings.configured()) {
            throw new IllegalStateException("Web Push ainda não está configurado no servidor.");
        }
        return settings.publicKey();
    }

    public void salvarAssinatura(String email, PushSubscriptionRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        PushSubscription subscription = subscriptionRepository.findByEndpoint(dto.getEndpoint())
                .orElseGet(PushSubscription::new);

        subscription.setUsuario(usuario);
        subscription.setEndpoint(dto.getEndpoint());
        subscription.setP256dh(dto.getP256dh());
        subscription.setAuth(dto.getAuth());

        subscriptionRepository.save(subscription);
    }

    public boolean enviarParaUsuario(String email, String titulo, String corpo, String url) {
        if (!settings.configured()) return false;

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        List<PushSubscription> subscriptions = subscriptionRepository.findByUsuarioId(usuario.getId());
        if (subscriptions.isEmpty()) return false;

        String payload;
        try {
            payload = objectMapper.writeValueAsString(new PushPayload(titulo, corpo, url));
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Não foi possível montar a notificação push.", exception);
        }

        boolean enviou = false;

        try {
            PushService pushService = new PushService(
                    settings.publicKey(),
                    settings.privateKey(),
                    settings.subject()
            );

            for (PushSubscription subscription : subscriptions) {
                try {
                    Notification notification = new Notification(
                        subscription.getEndpoint(),
                        subscription.getP256dh(),
                        subscription.getAuth(),
                        payload
                );

                HttpResponse response = pushService.send(notification);
                int status = response.getStatusLine().getStatusCode();

                    if (status == 404 || status == 410) {
                        subscriptionRepository.delete(subscription);
                    } else if (status >= 200 && status < 300) {
                        enviou = true;
                    }
                } catch (Exception exception) {
                    // Uma assinatura inválida não deve derrubar a operação principal da tarefa.
                }
            }
        } catch (Exception exception) {
            // Push indisponível não deve derrubar a operação principal da tarefa.
        }

        return enviou;
    }

    @Transactional
    @Scheduled(cron = "0 */15 * * * *")
    public void processarLembretesAutomaticos() {
        if (!settings.configured()) return;

        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(1);
        List<Tarefa> tarefas = tarefaRepository.findByStatusInAndDataEntregaLessThanEqual(
                List.of(StatusTarefa.PENDENTE, StatusTarefa.ATRASADA),
                limite
        );

        for (Tarefa tarefa : tarefas) {
            if (tarefa.getStatus() == StatusTarefa.PENDENTE && tarefa.getDataEntrega().isBefore(hoje)) {
                tarefa.setStatus(StatusTarefa.ATRASADA);
            }

            if (tarefa.getStatus() == StatusTarefa.ATRASADA && !tarefa.isLembreteAtrasoEnviado()) {
                boolean enviado = enviarParaUsuario(
                        tarefa.getUsuario().getEmail(),
                        "Tarefa atrasada",
                        tarefa.getTitulo(),
                        "/tarefas"
                );
                if (enviado) tarefa.setLembreteAtrasoEnviado(true);
            } else if (tarefa.getStatus() == StatusTarefa.PENDENTE
                    && !tarefa.isLembretePrazoEnviado()
                    && !tarefa.getDataEntrega().isAfter(limite)) {
                boolean enviado = enviarParaUsuario(
                        tarefa.getUsuario().getEmail(),
                        "Entrega em 24 horas",
                        tarefa.getTitulo(),
                        "/notificacoes"
                );
                if (enviado) tarefa.setLembretePrazoEnviado(true);
            }

            tarefaRepository.save(tarefa);
        }
    }

    private record PushPayload(String title, String body, String url) {}
}
