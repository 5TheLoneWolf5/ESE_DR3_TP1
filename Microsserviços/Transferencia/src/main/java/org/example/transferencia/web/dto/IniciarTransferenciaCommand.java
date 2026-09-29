package org.example.transferencia.web.dto;

import java.math.BigDecimal;

public record IniciarTransferenciaCommand(
        Long contaOrigemId,
        Long contaDestinoId,
        BigDecimal valor,
        String moeda
) {}
