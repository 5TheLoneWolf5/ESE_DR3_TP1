package org.example.conta.application;

import java.math.BigDecimal;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaRepository;
import org.example.conta.domain.value_objects.Saldo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarContaUseCase {

    private final ContaRepository contaRepository;

    public CriarContaUseCase(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @Transactional
    public Long execute(String nome, BigDecimal saldoInicial, String moeda) {
        Saldo saldo = new Saldo(saldoInicial != null ? saldoInicial : BigDecimal.ZERO, moeda != null ? moeda : "BRL");
        Conta conta = new Conta(nome, saldo);
        Conta salva = contaRepository.save(conta);
        return salva.getId();
    }
}
