package com.sgt.sgt_api.repository;

import com.sgt.sgt_api.entity.PushSubscription;
import com.sgt.sgt_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    Optional<PushSubscription> findByEndpoint(String endpoint);
    List<PushSubscription> findByUsuarioId(Long usuarioId);
}
