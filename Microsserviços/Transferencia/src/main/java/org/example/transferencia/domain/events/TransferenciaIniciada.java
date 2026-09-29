package org.example.transferencia.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferenciaIniciada(
        Long transferenciaId,
        Long contaOrigemId,
        Long contaDestinoId,
        BigDecimal valor,
        String moeda,
        Instant ocorridoEm
) {}
