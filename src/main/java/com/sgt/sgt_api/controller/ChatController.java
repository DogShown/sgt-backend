package com.sgt.sgt_api.controller;

import com.sgt.sgt_api.dto.request.MensagemChatDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    /**
     * O cliente (Front-end) envia para: /app/chat.enviar/{turma}
     * Todos os usuários inscritos em /topic/turma/{turma} recebem a mensagem instantaneamente.
     */
    @MessageMapping("/chat.enviar/{turma}")
    @SendTo("/topic/turma/{turma}")
    public MensagemChatDTO enviarMensagem(@DestinationVariable String turma, MensagemChatDTO mensagem) {
        mensagem.setTurma(turma);
        return mensagem;
    }
}