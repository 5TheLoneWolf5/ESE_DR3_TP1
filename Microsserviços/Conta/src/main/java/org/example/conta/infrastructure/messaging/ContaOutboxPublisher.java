package org.example.conta.infrastructure.messaging;

import java.util.List;
import org.example.conta.infrastructure.persistence.ContaOutboxMessage;
import org.example.conta.infrastructure.persistence.ContaOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ContaOutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(ContaOutboxPublisher.class);

    private final ContaOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ContaOutboxPublisher(ContaOutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publicarEventosPendentes() {
        List<ContaOutboxMessage> pendentes = outboxRepository.findByPublicadoFalseOrderByCriadoEmAsc();
        for (ContaOutboxMessage msg : pendentes) {
            try {
                kafkaTemplate.send("conta-events", String.valueOf(msg.getAggregateId()), msg.getPayload());
                msg.marcarComoPublicado();
                outboxRepository.save(msg);
                log.info("Evento publicado no Kafka a partir do ContaOutbox: id={}, tipo={}", msg.getId(), msg.getEventType());
            } catch (Exception e) {
                log.warn("Nao foi possivel publicar evento outbox {} no Kafka (tentara novamente): {}", msg.getId(), e.getMessage());
                break;
            }
        }
    }
}
