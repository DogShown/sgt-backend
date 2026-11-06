package com.sgt.sgt_api.repository;
import com.sgt.sgt_api.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription,Long>{
 Optional<PushSubscription> findByEndpoint(String endpoint);
 List<PushSubscription> findByUsuarioId(Long usuarioId);
 void deleteByUsuarioId(Long usuarioId);
}