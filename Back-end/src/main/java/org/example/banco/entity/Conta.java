package org.example.banco.entity;

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
    
    @Column(name = "saldo", nullable = false)
    private Double saldo;

    @Version
    private Long versao;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    public Conta() {
	}

    public Conta(String nome, Double saldo) {
    	this.nome = nome;
    	this.saldo = saldo;
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
        return id + " - " + nome + " - " + saldo;
    }
	
	public String getNome() {
		return this.nome;
	}
	
	public Double getSaldo() {
		return this.saldo;
	}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Conta)) return false;
		Conta conta = (Conta) o;
		return Objects.equals(id, conta.id) &&
				Objects.equals(nome, conta.nome) &&
				Objects.equals(saldo, conta.saldo);
	}
}
