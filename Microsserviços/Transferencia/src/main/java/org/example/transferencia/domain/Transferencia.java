package org.example.transferencia.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;
import org.example.transferencia.domain.events.TransferenciaIniciada;
import org.example.transferencia.domain.value_objects.Dinheiro;

@Entity
@Table(name="transferencia")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contaOrigemId", nullable = false)
    private Long contaOrigemId;

    @Column(name = "contaDestinoId", nullable = false)
    private Long contaDestinoId;

    @Column(name = "saldoValor", nullable = false)
    private BigDecimal saldoValor;

    @Column(name = "saldoMoeda", nullable = false)
    private String saldoMoeda;

    @Column(name = "status", nullable = false)
    private StatusTransferencia status;

    @Version
    private Long versao;

    protected Transferencia() {}

    public Transferencia(Long contaOrigemId, Long contaDestinoId, Dinheiro dinheiro) {

        if (contaOrigemId.equals(contaDestinoId)) {
            throw new IllegalArgumentException("Conta de origem não pode ser a mesma que a de destino.");
        }

        if (dinheiro.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Dinheiro de transferência deve ser maior que zero.");
        }

        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.saldoValor = dinheiro.valor();
        this.saldoMoeda = dinheiro.moeda();
        TransferenciaIniciada transferenciaIniciada = new TransferenciaIniciada(contaOrigemId, contaDestinoId, dinheiro, Instant.now());
        this.status = StatusTransferencia.INICIADA;
    }

    public StatusTransferencia getStatus() {
        return status;
    }

    public void setStatus(StatusTransferencia status) {
        this.status = status;
    }

    public String getSaldoMoeda() {
        return saldoMoeda;
    }

    public void setSaldoMoeda(String saldoMoeda) {
        this.saldoMoeda = saldoMoeda;
    }

    public BigDecimal getSaldoValor() {
        return saldoValor;
    }

    public void setSaldoValor(BigDecimal saldoValor) {
        this.saldoValor = saldoValor;
    }

    public Long getContaDestinoId() {
        return contaDestinoId;
    }

    public void setContaDestinoId(Long contaDestinoId) {
        this.contaDestinoId = contaDestinoId;
    }

    public Long getContaOrigemId() {
        return contaOrigemId;
    }

    public void setContaOrigemId(Long contaOrigemId) {
        this.contaOrigemId = contaOrigemId;
    }

	@Override
    public String toString() {
        return id + " - " + contaOrigemId + " - " + contaDestinoId + " - " +  saldoValor + " - " + saldoMoeda + " - " + status;
    }
}
