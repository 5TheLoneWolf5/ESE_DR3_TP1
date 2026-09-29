package org.example.transferencia.infrastructure.messaging;

import java.util.List;
import org.example.transferencia.infrastructure.outbox.OutboxMessage;
import org.example.transferencia.infrastructure.outbox.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransferenciaOutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaOutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransferenciaOutboxPublisher(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publicarEventosPendentes() {
        List<OutboxMessage> pendentes = outboxRepository.findByPublicadoFalseOrderByCriadoEmAsc();
        for (OutboxMessage msg : pendentes) {
            try {
                kafkaTemplate.send("transferencia-events", String.valueOf(msg.getAggregateId()), msg.getPayload());
                msg.marcarComoPublicado();
                outboxRepository.save(msg);
                log.info("Evento publicado no Kafka a partir do Outbox: id={}, tipo={}", msg.getId(), msg.getEventType());
            } catch (Exception e) {
                log.warn("Nao foi possivel publicar evento outbox {} no Kafka (tentara novamente): {}", msg.getId(), e.getMessage());
                break;
            }
        }
    }
}
