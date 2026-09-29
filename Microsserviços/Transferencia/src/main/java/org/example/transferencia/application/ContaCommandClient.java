package org.example.transferencia.application;

import java.math.BigDecimal;

public interface ContaCommandClient {
    void debitar(Long contaId, BigDecimal valor, String chaveIdempotencia, Long transferenciaId);
    void creditar(Long contaId, BigDecimal valor, String chaveIdempotencia, Long transferenciaId);
}
