package org.example.transferencia.domain.events;

import org.example.transferencia.domain.value_objects.Dinheiro;

import java.time.Instant;

public record TransferenciaIniciada(Long contaOrigemId, Long contaDestinoId, Dinheiro saldo, Instant ocorridoEm) {
}
