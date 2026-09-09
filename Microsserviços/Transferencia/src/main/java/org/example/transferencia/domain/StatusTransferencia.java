package org.example.transferencia.domain;

public enum StatusTransferencia {
    INICIADA,
    CONTA_ORIGEM_DEBITADA,
    DEBITO_FALHOU,
    CONCLUIDA, // CONTA_DESTINO_CREDITADA
    CREDITO_FALHOU,
    COMPENSADA,
    COMPENSACAO_FALHOU
}
