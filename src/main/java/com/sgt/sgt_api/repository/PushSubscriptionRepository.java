package com.sgt.sgt_api.repository;

import com.sgt.sgt_api.entity.PushSubscription;
import com.sgt.sgt_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    @Modifying
    @Query("delete from PushSubscription p where p.usuario.id = :usuarioId")
    int excluirTodasDoUsuario(@Param("usuarioId") Long usuarioId);

    Optional<PushSubscription> findByEndpoint(String endpoint);
    List<PushSubscription> findByUsuarioId(Long usuarioId);
}
