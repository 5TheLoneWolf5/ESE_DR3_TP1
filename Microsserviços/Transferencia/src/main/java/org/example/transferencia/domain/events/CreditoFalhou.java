package org.example.transferencia.domain.events;

import java.time.Instant;

public record CreditoFalhou(
        Long transferenciaId,
        String motivo,
        Instant ocorridoEm
) {}
