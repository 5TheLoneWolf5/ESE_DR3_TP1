package org.example.conta.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

public record ContaDebitada(
        Long contaId,
        BigDecimal valor,
        String moeda,
        String chaveIdempotencia,
        Long transferenciaId,
        Instant ocorridoEm
) {}
