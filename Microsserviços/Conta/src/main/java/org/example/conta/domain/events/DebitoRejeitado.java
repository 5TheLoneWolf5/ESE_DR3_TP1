package org.example.conta.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

public record DebitoRejeitado(
        Long contaId,
        BigDecimal valor,
        String motivo,
        String chaveIdempotencia,
        Long transferenciaId,
        Instant ocorridoEm
) {}
