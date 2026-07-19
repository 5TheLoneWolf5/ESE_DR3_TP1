package org.example.banco.entity;

import jakarta.persistence.*;
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

    @Column(name = "saldo", nullable = false)
    private Double saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacao", nullable = false)
    private TipoOperacao tipoOperacao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public ContaHistorico() {
    }

    public ContaHistorico(Long contaId, String nome, Double saldo, TipoOperacao tipoOperacao, LocalDateTime dataHora) {
        this.contaId = contaId;
        this.nome = nome;
        this.saldo = saldo;
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

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
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
                Objects.equals(saldo, that.saldo) &&
                tipoOperacao == that.tipoOperacao &&
                Objects.equals(dataHora, that.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contaId, nome, saldo, tipoOperacao, dataHora);
    }

    @Override
    public String toString() {
        return "ContaHistorico{" +
                "id=" + id +
                ", contaId=" + contaId +
                ", nome='" + nome + '\'' +
                ", saldo=" + saldo +
                ", tipoOperacao=" + tipoOperacao +
                ", dataHora=" + dataHora +
                '}';
    }
}
