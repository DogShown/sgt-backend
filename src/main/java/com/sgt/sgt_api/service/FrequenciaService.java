package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.RegistroFrequenciaRequestDTO;
import com.sgt.sgt_api.dto.request.ConfiguracaoFrequenciaRequestDTO;
import com.sgt.sgt_api.dto.response.RegistroFrequenciaResponseDTO;
import com.sgt.sgt_api.dto.response.ConfiguracaoFrequenciaResponseDTO;
import com.sgt.sgt_api.entity.*;
import com.sgt.sgt_api.repository.*;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FrequenciaService {
    private final RegistroFrequenciaRepository registros;
    private final UsuarioRepository usuarios;
    private final ConfiguracaoFrequenciaRepository configuracoes;
    public FrequenciaService(RegistroFrequenciaRepository registros, UsuarioRepository usuarios, ConfiguracaoFrequenciaRepository configuracoes){this.registros=registros;this.usuarios=usuarios;this.configuracoes=configuracoes;}
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

    public ConfiguracaoFrequenciaResponseDTO obterConfiguracao(String email) {
        Usuario u=usuario(email);
        return configuracoes.findByUsuarioId(u.getId()).map(this::mapearConfiguracao).orElse(null);
    }
    public ConfiguracaoFrequenciaResponseDTO salvarConfiguracao(ConfiguracaoFrequenciaRequestDTO dto,String email) {
        Usuario u=usuario(email);
        ConfiguracaoFrequencia c=configuracoes.findByUsuarioId(u.getId()).orElseGet(ConfiguracaoFrequencia::new);
        c.setUsuario(u); c.setLimite(dto.limite()); c.setTotalPrevisto(dto.totalPrevisto());
        c.setUnidade(dto.unidade()); c.setDisciplinasAtivas(dto.disciplinasAtivas());
        List<String> materias=(dto.disciplinas()==null?List.<String>of():dto.disciplinas()).stream()
            .map(String::trim).filter(v->!v.isBlank()).distinct().toList();
        c.setDisciplinasSerializadas(materias.stream().map(v->Base64.getUrlEncoder().withoutPadding().encodeToString(v.getBytes(StandardCharsets.UTF_8))).collect(Collectors.joining(".")));
        return mapearConfiguracao(configuracoes.save(c));
    }
    private ConfiguracaoFrequenciaResponseDTO mapearConfiguracao(ConfiguracaoFrequencia c) {
        try {
            String serializado=c.getDisciplinasSerializadas();
            List<String> materias=serializado==null || serializado.isBlank()?List.of():Arrays.stream(serializado.split("\\.")).map(v->new String(Base64.getUrlDecoder().decode(v),StandardCharsets.UTF_8)).toList();
            return new ConfiguracaoFrequenciaResponseDTO(c,materias);
        } catch(Exception e){throw new IllegalStateException("Não foi possível ler as matérias da frequência.",e);}
    }
    private Usuario usuario(String email){return usuarios.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Usuário não encontrado."));}
}