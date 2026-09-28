package com.sgt.sgt_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Tópicos onde os clientes se inscrevem para receber mensagens em tempo real
        config.enableSimpleBroker("/topic");

        // Prefixo para as rotas onde os clientes enviam mensagens do front-end
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint do WebSocket com suporte a SockJS (Fallback caso a conexão WS pura seja bloqueada)
        registry.addEndpoint("/ws-sgt")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}