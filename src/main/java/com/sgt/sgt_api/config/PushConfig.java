package com.sgt.sgt_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PushConfig {

    @Value("${sgt.push.public-key:}")
    private String publicKey;

    @Value("${sgt.push.private-key:}")
    private String privateKey;

    @Value("${sgt.push.subject:mailto:admin@sgt.local}")
    private String subject;

    @Bean
    public PushSettings pushSettings() {
        return new PushSettings(publicKey, privateKey, subject);
    }

    public record PushSettings(String publicKey, String privateKey, String subject) {
        public boolean configured() {
            return publicKey != null && !publicKey.isBlank()
                    && privateKey != null && !privateKey.isBlank();
        }
    }
}
