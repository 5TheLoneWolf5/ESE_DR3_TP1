package org.example.transferencia.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.example.transferencia.domain.Transferencia;
import org.example.transferencia.domain.TransferenciaRepository;
import org.example.transferencia.domain.value_objects.Dinheiro;
import org.example.transferencia.infrastructure.outbox.OutboxMessage;
import org.example.transferencia.infrastructure.outbox.OutboxRepository;
import org.example.transferencia.web.dto.IniciarTransferenciaCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IniciarTransferenciaUseCase {

    private static final Logger log = LoggerFactory.getLogger(IniciarTransferenciaUseCase.class);

    private final TransferenciaRepository transferenciaRepository;
    private final OutboxRepository outboxRepository;
    private final ContaCommandClient contaCommandClient;
    private final ObjectMapper objectMapper;

    public IniciarTransferenciaUseCase(
            TransferenciaRepository transferenciaRepository,
            OutboxRepository outboxRepository,
            ContaCommandClient contaCommandClient,
            ObjectMapper objectMapper
    ) {
        this.transferenciaRepository = transferenciaRepository;
        this.outboxRepository = outboxRepository;
        this.contaCommandClient = contaCommandClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Long execute(IniciarTransferenciaCommand command) {
        log.info("Iniciando transferencia da conta {} para {} no valor de {} {}",
                command.contaOrigemId(), command.contaDestinoId(), command.valor(), command.moeda());

        Dinheiro dinheiro = new Dinheiro(command.valor(), command.moeda());
        Transferencia t = new Transferencia(command.contaOrigemId(), command.contaDestinoId(), dinheiro);

        t = transferenciaRepository.save(t);

        List<Object> eventos = t.eventosNaoPublicados();
        for (Object evento : eventos) {
            try {
                String payload = objectMapper.writeValueAsString(evento);
                OutboxMessage msg = new OutboxMessage("Transferencia", t.getId(), evento.getClass().getSimpleName(), payload);
                outboxRepository.save(msg);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao serializar evento para outbox", e);
            }
        }
        t.limparEventos();

        String chaveIdempotencia = "transferencia-" + t.getId() + "-debito";
        try {
            contaCommandClient.debitar(t.getContaOrigemId(), t.getValor(), chaveIdempotencia, t.getId());
        } catch (Exception e) {
            log.warn("Falha imediata ao chamar debito em Conta para transferencia {}: {}", t.getId(), e.getMessage());
            t.falharDebito();
            transferenciaRepository.save(t);
        }

        return t.getId();
    }
}
