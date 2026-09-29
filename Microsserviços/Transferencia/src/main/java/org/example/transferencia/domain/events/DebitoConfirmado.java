package org.example.transferencia.domain.events;

import java.time.Instant;

public record DebitoConfirmado(
        Long transferenciaId,
        Instant ocorridoEm
) {}
