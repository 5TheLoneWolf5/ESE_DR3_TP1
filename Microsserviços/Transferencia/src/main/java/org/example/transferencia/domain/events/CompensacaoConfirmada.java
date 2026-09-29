package org.example.transferencia.domain.events;

import java.time.Instant;

public record CompensacaoConfirmada(
        Long transferenciaId,
        Instant ocorridoEm
) {}
