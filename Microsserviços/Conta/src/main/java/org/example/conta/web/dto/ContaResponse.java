package org.example.conta.web.dto;

import java.math.BigDecimal;
import org.example.conta.domain.Conta;

public record ContaResponse(
        Long id,
        String nome,
        BigDecimal saldoValor,
        String saldoMoeda,
        Long versao
) {
    public static ContaResponse from(Conta c) {
        return new ContaResponse(
                c.getId(),
                c.getNome(),
                c.getSaldoValor(),
                c.getSaldoMoeda(),
                c.getVersao()
        );
    }
}
