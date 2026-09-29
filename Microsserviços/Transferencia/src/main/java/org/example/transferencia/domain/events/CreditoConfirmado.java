package org.example.transferencia.domain.events;

import java.time.Instant;

public record CreditoConfirmado(
        Long transferenciaId,
        Instant ocorridoEm
) {}
