package org.example.banco.entity;

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
    private String nome;
    private Double saldo;
    
    public Conta() {
	}

    public Conta(String nome, Double saldo) {
    	this.nome = nome;
    	this.saldo = saldo;
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
