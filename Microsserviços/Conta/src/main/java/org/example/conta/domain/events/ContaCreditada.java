package org.example.conta.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

public record ContaCreditada(
        Long contaId,
        BigDecimal valor,
        String moeda,
        String chaveIdempotencia,
        Long transferenciaId,
        Instant ocorridoEm
) {}
