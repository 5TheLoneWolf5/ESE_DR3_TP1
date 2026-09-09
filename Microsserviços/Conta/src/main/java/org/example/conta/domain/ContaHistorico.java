package org.example.conta.domain;

import jakarta.persistence.*;
import org.example.conta.domain.value_objects.Saldo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "conta_historico")
public class ContaHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "saldo_valor", nullable = false)
    private BigDecimal saldoValor;

    @Column(name = "saldo_moeda", nullable = false)
    private String saldoMoeda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacao", nullable = false)
    private TipoOperacao tipoOperacao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public ContaHistorico() {
    }

    public ContaHistorico(Long contaId, String nome, String senha, Saldo saldo, TipoOperacao tipoOperacao, LocalDateTime dataHora) {
        this.contaId = contaId;
        this.nome = nome;
        this.senha = senha;
        this.saldoValor = saldo.valor();
        this.saldoMoeda = saldo.moeda();
        this.tipoOperacao = tipoOperacao;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return nome;
    }

    public void setSenha(String senha) {
        this.nome = nome;
    }

    public BigDecimal getSaldoValor() {
        return this.saldoValor;
    }

    public String getSaldoMoeda() {
        return this.saldoMoeda;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaHistorico that = (ContaHistorico) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(contaId, that.contaId) &&
                Objects.equals(nome, that.nome) &&
                Objects.equals(saldoValor, that.saldoValor) &&
                Objects.equals(saldoMoeda, that.saldoMoeda) &&
                tipoOperacao == that.tipoOperacao &&
                Objects.equals(dataHora, that.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contaId, nome, saldoValor, saldoMoeda, tipoOperacao, dataHora);
    }

    @Override
    public String toString() {
        return "ContaHistorico{" +
                "id=" + id +
                ", contaId=" + contaId +
                ", nome='" + nome + '\'' +
                ", saldoValor=" + saldoValor +
                ", saldoMoeda=" + saldoMoeda +
                ", tipoOperacao=" + tipoOperacao +
                ", dataHora=" + dataHora +
                '}';
    }
}
