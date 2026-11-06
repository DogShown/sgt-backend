package com.sgt.sgt_api.repository;
import com.sgt.sgt_api.entity.TokenConta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface TokenContaRepository extends JpaRepository<TokenConta,Long>{
 Optional<TokenConta> findByTokenHashAndFinalidadeAndConsumidoEmIsNull(String hash,String finalidade);
 void deleteByUsuarioIdAndFinalidadeAndConsumidoEmIsNull(Long usuarioId,String finalidade);
 void deleteByUsuarioId(Long usuarioId);
}