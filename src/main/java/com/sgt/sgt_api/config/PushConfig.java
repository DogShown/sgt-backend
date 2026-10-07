package com.sgt.sgt_api.config;

import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class PushConfig {

    @Value("${sgt.push.public-key:}")
    private String publicKey;

    @Value("${sgt.push.private-key:}")
    private String privateKey;

    @Value("${sgt.push.subject:mailto:admin@sgt.local}")
    private String subject;

    @Bean
    public PushService pushService() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        if (publicKey == null || publicKey.isBlank() || privateKey == null || privateKey.isBlank()) {
            return null;
        }

        return new PushService()
                .setPublicKey(Utils.loadPublicKey(publicKey))
                .setPrivateKey(Utils.loadPrivateKey(privateKey))
                .setSubject(subject);
    }

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
