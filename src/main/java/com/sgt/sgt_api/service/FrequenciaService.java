package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.RegistroFrequenciaRequestDTO;
import com.sgt.sgt_api.dto.response.RegistroFrequenciaResponseDTO;
import com.sgt.sgt_api.entity.*;
import com.sgt.sgt_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FrequenciaService {
    private final RegistroFrequenciaRepository registros;
    private final UsuarioRepository usuarios;
    public FrequenciaService(RegistroFrequenciaRepository registros, UsuarioRepository usuarios){this.registros=registros;this.usuarios=usuarios;}
    public List<RegistroFrequenciaResponseDTO> listar(String email){
        Usuario u=usuario(email);
        return registros.findByUsuarioIdOrderByDataDesc(u.getId()).stream().map(RegistroFrequenciaResponseDTO::new).toList();
    }
    public RegistroFrequenciaResponseDTO criar(RegistroFrequenciaRequestDTO dto,String email){
        RegistroFrequencia r=new RegistroFrequencia(); r.setUsuario(usuario(email)); r.setData(dto.data()); r.setQuantidade(dto.quantidade());
        r.setMateria(dto.materia()==null?null:dto.materia().trim()); r.setJustificada(dto.justificada()); r.setUnidade(dto.unidade());
        return new RegistroFrequenciaResponseDTO(registros.save(r));
    }
    public void excluir(Long id,String email){
        RegistroFrequencia r=registros.findById(id).orElseThrow(()->new IllegalArgumentException("Registro não encontrado."));
        if(!r.getUsuario().getEmail().equalsIgnoreCase(email)) throw new IllegalArgumentException("Sem permissão para excluir este registro.");
        registros.delete(r);
    }
    private Usuario usuario(String email){return usuarios.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Usuário não encontrado."));}
}