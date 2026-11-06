package com.sgt.sgt_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.*;
import org.springframework.web.socket.config.annotation.*;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ChatAuthenticationInterceptor chatAuthenticationInterceptor;
    private final List<String> allowedOrigins;
    public WebSocketConfig(ChatAuthenticationInterceptor interceptor, @Value("${sgt.cors.allowed-origins}") String origins) {
        this.chatAuthenticationInterceptor=interceptor;
        this.allowedOrigins=Arrays.stream(origins.split(",")).map(String::trim).filter(s->!s.isEmpty()).toList();
    }
    @Override public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    @Override public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatAuthenticationInterceptor);
    }
    @Override public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-sgt").setAllowedOriginPatterns(allowedOrigins.toArray(String[]::new)).withSockJS();
    }
}