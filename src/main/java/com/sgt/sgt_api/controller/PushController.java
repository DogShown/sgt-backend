package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.PushSubscriptionRequestDTO;
import com.sgt.sgt_api.service.PushNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private final PushNotificationService pushNotificationService;

    public PushController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @GetMapping("/public-key")
    public ResponseEntity<String> publicKey() {
        try {
            return ResponseEntity.ok(pushNotificationService.getPublicKey());
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.getMessage());
        }
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<Void> saveSubscription(
            Authentication authentication,
            @Valid @RequestBody PushSubscriptionRequestDTO dto
    ) {
        pushNotificationService.salvarAssinatura(authentication.getName(), dto);
        return ResponseEntity.noContent().build();
    }
}
