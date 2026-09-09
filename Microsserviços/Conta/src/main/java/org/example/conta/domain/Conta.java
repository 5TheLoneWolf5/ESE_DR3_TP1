package org.example.conta.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name="conta")
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "senha", nullable = false)
    private String senha;

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
    
    public Conta() {
	}

    public Conta(String nome, String senha) {
    	this.nome = nome;
        this.senha = senha;
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

	@Override
    public String toString() {
        return id + " - " + nome + " - " + saldoValor + " - " + saldoMoeda;
    }
	
	public String getNome() {
		return this.nome;
	}
	
	public BigDecimal getSaldoValor() {
		return this.saldoValor;
	}

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSaldoMoeda() {
        return this.saldoMoeda;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

	public Long getVersao() {
		return versao;
	}

	public void setVersao(Long versao) {
		this.versao = versao;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public LocalDateTime getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

    public void debitar(BigDecimal valor) throws IllegalArgumentException {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve debitar um valor maior que zero.");
        }
        BigDecimal valorDebitado = saldoValor.subtract(valor);
        this.saldoValor = valorDebitado;

        if (saldoValor.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Conta em dívida."); // Implementar notificações e outras funcionalidades no futuro.
        }
    }

    public void creditar(BigDecimal valor) throws IllegalArgumentException {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de crédito deve ser positivo.");
        }
        this.saldoValor = saldoValor.add(valor);
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Conta)) return false;
		Conta conta = (Conta) o;
		return Objects.equals(id, conta.id) &&
				Objects.equals(nome, conta.nome) &&
				Objects.equals(saldoValor, conta.saldoValor) &&
                Objects.equals(saldoMoeda, conta.saldoMoeda);
	}

}
