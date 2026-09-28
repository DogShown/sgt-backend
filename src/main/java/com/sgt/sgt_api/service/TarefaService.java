package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.TarefaRequestDTO;
import com.sgt.sgt_api.dto.response.TarefaResponseDTO;
import com.sgt.sgt_api.entity.Tarefa;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.enums.StatusTarefa;
import com.sgt.sgt_api.repository.TarefaRepository;
import com.sgt.sgt_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    public TarefaService(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public TarefaResponseDTO criar(TarefaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setCategoria(dto.categoria());
        tarefa.setPrioridade(dto.prioridade());
        tarefa.setDataEntrega(dto.dataEntrega());
        tarefa.setUsuario(usuario);
        tarefa.setStatus(StatusTarefa.PENDENTE);

        tarefa = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(tarefa);
    }

    public List<TarefaResponseDTO> listarPorUsuario(Long usuarioId) {
        List<Tarefa> tarefas = tarefaRepository.findByUsuarioId(usuarioId);

        // Atualiza tarefas vencidas automaticamente para ATRASADA
        tarefas.forEach(t -> {
            if (t.getStatus() == StatusTarefa.PENDENTE && LocalDate.now().isAfter(t.getDataEntrega())) {
                t.setStatus(StatusTarefa.ATRASADA);
                tarefaRepository.save(t);
            }
        });

        return tarefas.stream().map(TarefaResponseDTO::new).toList();
    }

    public TarefaResponseDTO concluirTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        if (LocalDate.now().isAfter(tarefa.getDataEntrega())) {
            tarefa.setStatus(StatusTarefa.CONCLUIDA_COM_ATRASO);
        } else {
            tarefa.setStatus(StatusTarefa.CONCLUIDA_NO_PRAZO);
        }

        tarefa = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(tarefa);
    }

    public void deletar(Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new RuntimeException("Tarefa não encontrada.");
        }
        tarefaRepository.deleteById(id);
    }
}