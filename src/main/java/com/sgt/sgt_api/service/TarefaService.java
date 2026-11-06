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

@Service @Transactional
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PushNotificationService pushNotificationService;
    public TarefaService(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository, PushNotificationService pushNotificationService) {
        this.tarefaRepository=tarefaRepository; this.usuarioRepository=usuarioRepository; this.pushNotificationService=pushNotificationService;
    }
    public TarefaResponseDTO criar(TarefaRequestDTO dto,String emailUsuario) {
        if(dto.dataEntrega().isBefore(LocalDate.now())) throw new IllegalArgumentException("A data de entrega deve ser atual ou futura ao criar uma tarefa.");
        Usuario usuario=buscarUsuarioAutenticado(emailUsuario);
        Tarefa tarefa=new Tarefa(); aplicarDados(tarefa,dto); tarefa.setUsuario(usuario); tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa=tarefaRepository.save(tarefa);
        pushNotificationService.enviarParaUsuario(emailUsuario,"Nova tarefa criada",tarefa.getTitulo(),"/tarefas");
        return new TarefaResponseDTO(tarefa);
    }
    public List<TarefaResponseDTO> listarPorUsuario(String emailUsuario) {
        Usuario usuario=buscarUsuarioAutenticado(emailUsuario);
        List<Tarefa> tarefas=tarefaRepository.findByUsuarioId(usuario.getId());
        tarefas.forEach(t->{if((t.getStatus()==StatusTarefa.PENDENTE || t.getStatus()==StatusTarefa.EM_ANDAMENTO) && LocalDate.now().isAfter(t.getDataEntrega())) {t.setStatus(StatusTarefa.ATRASADA);tarefaRepository.save(t);}});
        return tarefas.stream().map(TarefaResponseDTO::new).toList();
    }
    public TarefaResponseDTO atualizar(Long id,String emailUsuario,TarefaRequestDTO dto) {
        Tarefa tarefa=buscarTarefaDoUsuario(id,emailUsuario);
        aplicarDados(tarefa,dto);
        return new TarefaResponseDTO(tarefaRepository.save(tarefa));
    }
    private void aplicarDados(Tarefa tarefa,TarefaRequestDTO dto) {
        tarefa.setTitulo(dto.titulo().trim()); tarefa.setDescricao(dto.descricao()==null?null:dto.descricao().trim());
        tarefa.setCategoria(dto.categoria()); tarefa.setPrioridade(dto.prioridade()); tarefa.setDataEntrega(dto.dataEntrega());
    }
    public TarefaResponseDTO atualizarStatus(Long id,String emailUsuario,StatusTarefa novoStatus) {
        if(novoStatus!=StatusTarefa.PENDENTE && novoStatus!=StatusTarefa.EM_ANDAMENTO) throw new IllegalArgumentException("Status inválido para esta operação.");
        Tarefa tarefa=buscarTarefaDoUsuario(id,emailUsuario);
        if(tarefa.getStatus()==StatusTarefa.CONCLUIDA_NO_PRAZO || tarefa.getStatus()==StatusTarefa.CONCLUIDA_COM_ATRASO) throw new IllegalStateException("Uma tarefa concluída não pode voltar para andamento.");
        tarefa.setStatus(novoStatus); return new TarefaResponseDTO(tarefaRepository.save(tarefa));
    }
    public TarefaResponseDTO concluirTarefa(Long id,String emailUsuario) {
        Tarefa tarefa=buscarTarefaDoUsuario(id,emailUsuario);
        tarefa.setStatus(LocalDate.now().isAfter(tarefa.getDataEntrega())?StatusTarefa.CONCLUIDA_COM_ATRASO:StatusTarefa.CONCLUIDA_NO_PRAZO);
        tarefa=tarefaRepository.save(tarefa);
        pushNotificationService.enviarParaUsuario(emailUsuario,"Tarefa concluída",tarefa.getTitulo(),"/tarefas");
        return new TarefaResponseDTO(tarefa);
    }
    public void deletar(Long id,String emailUsuario){tarefaRepository.delete(buscarTarefaDoUsuario(id,emailUsuario));}
    private Usuario buscarUsuarioAutenticado(String email){return usuarioRepository.findByEmail(email).orElseThrow(()->new RuntimeException("Usuário autenticado não encontrado."));}
    private Tarefa buscarTarefaDoUsuario(Long id,String emailUsuario){
        Tarefa tarefa=tarefaRepository.findById(id).orElseThrow(()->new RuntimeException("Tarefa não encontrada."));
        if(!tarefa.getUsuario().getEmail().equalsIgnoreCase(emailUsuario)) throw new RuntimeException("Você não tem permissão para acessar esta tarefa.");
        return tarefa;
    }
}