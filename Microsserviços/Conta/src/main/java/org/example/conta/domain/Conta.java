package org.example.conta.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.example.conta.domain.events.ContaCreditada;
import org.example.conta.domain.events.ContaDebitada;
import org.example.conta.domain.value_objects.Saldo;

@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "saldo_valor", nullable = false)
    private BigDecimal saldoValor;

    @Column(name = "saldo_moeda", nullable = false)
    private String saldoMoeda;

    @Version
    private Long versao;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Transient
    private final List<Object> eventosNaoPublicados = new ArrayList<>();

    protected Conta() {
    }

    public Conta(String nome, Saldo saldoInicial) {
        Objects.requireNonNull(nome, "Nome da conta não pode ser nulo.");
        Objects.requireNonNull(saldoInicial, "Saldo inicial não pode ser nulo.");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome da conta não pode ser vazio.");
        }
        this.nome = nome;
        this.saldoValor = saldoInicial.valor();
        this.saldoMoeda = saldoInicial.moeda();
    }

    @Deprecated
    public Conta(String nome, String senha) {
        this(nome, new Saldo(BigDecimal.ZERO, "BRL"));
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Saldo getSaldo() {
        return new Saldo(saldoValor, saldoMoeda);
    }

    public void debitar(BigDecimal valor) {
        debitar(valor, null, null);
    }

    public void debitar(BigDecimal valor, String chaveIdempotencia, Long transferenciaId) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de débito deve ser maior que zero.");
        }
        if (saldoValor.subtract(valor).compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException(
                    String.format("Saldo insuficiente na conta %d. Saldo atual: %s, Solicitado: %s", id, saldoValor, valor)
            );
        }
        this.saldoValor = this.saldoValor.subtract(valor);
        this.eventosNaoPublicados.add(new ContaDebitada(
                this.id,
                valor,
                this.saldoMoeda,
                chaveIdempotencia,
                transferenciaId,
                Instant.now()
        ));
    }

    public void creditar(BigDecimal valor) {
        creditar(valor, null, null);
    }

    public void creditar(BigDecimal valor, String chaveIdempotencia, Long transferenciaId) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de crédito deve ser maior que zero.");
        }
        this.saldoValor = this.saldoValor.add(valor);
        this.eventosNaoPublicados.add(new ContaCreditada(
                this.id,
                valor,
                this.saldoMoeda,
                chaveIdempotencia,
                transferenciaId,
                Instant.now()
        ));
    }

    public List<Object> eventosNaoPublicados() {
        List<Object> eventosAjustados = new ArrayList<>();
        for (Object evento : this.eventosNaoPublicados) {
            if (evento instanceof ContaDebitada cd && cd.contaId() == null && this.id != null) {
                eventosAjustados.add(new ContaDebitada(this.id, cd.valor(), cd.moeda(), cd.chaveIdempotencia(), cd.transferenciaId(), cd.ocorridoEm()));
            } else if (evento instanceof ContaCreditada cc && cc.contaId() == null && this.id != null) {
                eventosAjustados.add(new ContaCreditada(this.id, cc.valor(), cc.moeda(), cc.chaveIdempotencia(), cc.transferenciaId(), cc.ocorridoEm()));
            } else {
                eventosAjustados.add(evento);
            }
        }
        return Collections.unmodifiableList(eventosAjustados);
    }

    public void limparEventos() {
        this.eventosNaoPublicados.clear();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getSaldoValor() {
        return saldoValor;
    }

    public String getSaldoMoeda() {
        return saldoMoeda;
    }

    public Long getVersao() {
        return versao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return id != null && id.equals(conta.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", saldoValor=" + saldoValor +
                ", saldoMoeda='" + saldoMoeda + '\'' +
                ", versao=" + versao +
                '}';
    }
}
