package org.example.conta.infrastructure.persistence;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chave_idempotencia")
public class ChaveIdempotencia {

    @Id
    @Column(name = "chave", length = 120, nullable = false)
    private String chave;

    @Column(name = "operacao", nullable = false)
    private String operacao;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Column(name = "resultado", nullable = false)
    private String resultado;

    @Column(name = "processado_em", nullable = false)
    private Instant processadoEm;

    protected ChaveIdempotencia() {
    }

    public ChaveIdempotencia(String chave, String operacao, Long contaId, String resultado) {
        this.chave = chave;
        this.operacao = operacao;
        this.contaId = contaId;
        this.resultado = resultado;
        this.processadoEm = Instant.now();
    }

    public String getChave() {
        return chave;
    }

    public String getOperacao() {
        return operacao;
    }

    public Long getContaId() {
        return contaId;
    }

    public String getResultado() {
        return resultado;
    }

    public Instant getProcessadoEm() {
        return processadoEm;
    }
}
