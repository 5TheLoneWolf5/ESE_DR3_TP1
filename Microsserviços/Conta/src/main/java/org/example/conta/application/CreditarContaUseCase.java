package org.example.conta.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaRepository;
import org.example.conta.infrastructure.persistence.ChaveIdempotencia;
import org.example.conta.infrastructure.persistence.ChaveIdempotenciaRepository;
import org.example.conta.infrastructure.persistence.ContaOutboxMessage;
import org.example.conta.infrastructure.persistence.ContaOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditarContaUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreditarContaUseCase.class);

    private final ContaRepository contaRepository;
    private final ContaOutboxRepository outboxRepository;
    private final ChaveIdempotenciaRepository idempotenciaRepository;
    private final ObjectMapper objectMapper;

    public CreditarContaUseCase(
            ContaRepository contaRepository,
            ContaOutboxRepository outboxRepository,
            ChaveIdempotenciaRepository idempotenciaRepository,
            ObjectMapper objectMapper
    ) {
        this.contaRepository = contaRepository;
        this.outboxRepository = outboxRepository;
        this.idempotenciaRepository = idempotenciaRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void execute(Long contaId, BigDecimal valor, String chaveIdempotencia, Long transferenciaId) {
        if (chaveIdempotencia != null && !chaveIdempotencia.isBlank()) {
            if (idempotenciaRepository.existsById(chaveIdempotencia)) {
                log.info("Comando de credito duplicado ignorado (chave idempotente ja processada: {})", chaveIdempotencia);
                return;
            }
        }

        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada com o ID: " + contaId));

        try {
            conta.creditar(valor, chaveIdempotencia, transferenciaId);
            contaRepository.save(conta);

            List<Object> eventos = conta.eventosNaoPublicados();
            for (Object evento : eventos) {
                String payload = objectMapper.writeValueAsString(evento);
                ContaOutboxMessage outbox = new ContaOutboxMessage("Conta", conta.getId(), evento.getClass().getSimpleName(), payload);
                outboxRepository.save(outbox);
            }
            conta.limparEventos();

            if (chaveIdempotencia != null && !chaveIdempotencia.isBlank()) {
                idempotenciaRepository.save(new ChaveIdempotencia(chaveIdempotencia, "CREDITO", contaId, "SUCESSO"));
            }
        } catch (Exception e) {
            log.error("Erro inesperado ao creditar conta {}: {}", contaId, e.getMessage(), e);
            throw new RuntimeException("Erro ao processar crédito: " + e.getMessage(), e);
        }
    }
}
