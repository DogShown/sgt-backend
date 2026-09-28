package com.sgt.sgt_api.service;

import com.sgt.sgt_api.dto.request.TarefaRequestDTO;
import com.sgt.sgt_api.dto.response.TarefaResponseDTO;
import com.sgt.sgt_api.entity.Tarefa;
import com.sgt.sgt_api.entity.Usuario;
import com.sgt.sgt_api.enums.StatusTarefa;
import com.sgt.sgt_api.repository.TarefaRepository;
import com.sgt.sgt_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    public TarefaService(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public TarefaResponseDTO criar(TarefaRequestDTO dto, String emailUsuario) {
        Usuario usuario = buscarUsuarioAutenticado(emailUsuario);

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

    public List<TarefaResponseDTO> listarPorUsuario(String emailUsuario) {
        Usuario usuario = buscarUsuarioAutenticado(emailUsuario);
        List<Tarefa> tarefas = tarefaRepository.findByUsuarioId(usuario.getId());

        tarefas.forEach(t -> {
            if (t.getStatus() == StatusTarefa.PENDENTE && LocalDate.now().isAfter(t.getDataEntrega())) {
                t.setStatus(StatusTarefa.ATRASADA);
                tarefaRepository.save(t);
            }
        });

        return tarefas.stream().map(TarefaResponseDTO::new).toList();
    }

    public TarefaResponseDTO concluirTarefa(Long id, String emailUsuario) {
        Tarefa tarefa = buscarTarefaDoUsuario(id, emailUsuario);

        if (LocalDate.now().isAfter(tarefa.getDataEntrega())) {
            tarefa.setStatus(StatusTarefa.CONCLUIDA_COM_ATRASO);
        } else {
            tarefa.setStatus(StatusTarefa.CONCLUIDA_NO_PRAZO);
        }

        tarefa = tarefaRepository.save(tarefa);
        return new TarefaResponseDTO(tarefa);
    }

    public void deletar(Long id, String emailUsuario) {
        Tarefa tarefa = buscarTarefaDoUsuario(id, emailUsuario);
        tarefaRepository.delete(tarefa);
    }

    private Usuario buscarUsuarioAutenticado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));
    }

    private Tarefa buscarTarefaDoUsuario(Long id, String emailUsuario) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        if (!tarefa.getUsuario().getEmail().equalsIgnoreCase(emailUsuario)) {
            throw new RuntimeException("Você não tem permissão para acessar esta tarefa.");
        }

        return tarefa;
    }
}