package com.sgt.sgt_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgt.sgt_api.config.PushConfig.PushSettings;
import com.sgt.sgt_api.dto.request.PushSubscriptionRequestDTO;
import com.sgt.sgt_api.entity.PushSubscription;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.repository.PushSubscriptionRepository;
import com.sgt.sgt_api.repository.UsuarioRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PushNotificationService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PushSettings settings;
    private final ObjectMapper objectMapper;

    public PushNotificationService(
            PushSubscriptionRepository subscriptionRepository,
            UsuarioRepository usuarioRepository,
            PushSettings settings,
            ObjectMapper objectMapper
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.usuarioRepository = usuarioRepository;
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

    public void enviarParaUsuario(String email, String titulo, String corpo, String url) {
        if (!settings.configured()) return;

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        List<PushSubscription> subscriptions = subscriptionRepository.findByUsuarioId(usuario.getId());

        String payload;
        try {
            payload = objectMapper.writeValueAsString(new PushPayload(titulo, corpo, url));
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Não foi possível montar a notificação push.", exception);
        }

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
                    }
                } catch (Exception exception) {
                    // Uma assinatura inválida não deve derrubar a operação principal da tarefa.
                }
            }
        } catch (Exception exception) {
            // Push indisponível não deve derrubar a operação principal da tarefa.
        }
    }

    private record PushPayload(String title, String body, String url) {}
}
