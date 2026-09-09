package org.example.conta.application;

import org.example.conta.domain.ContaRepository;
import org.example.conta.domain.ContaHistoricoRepository;
import org.example.conta.domain.value_objects.Saldo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaHistorico;
import org.example.conta.domain.TipoOperacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Fail early aplicado no back-end e fail gracefully no front-end (com mensagens claras e seguras).

@Service
@Transactional
public class ContaService {
    private final ContaRepository contaRepository;
    private final ContaHistoricoRepository contaHistoricoRepository;

    private final PasswordEncoder passwordEncoder;

    public ContaService(ContaRepository contaRepository, ContaHistoricoRepository contaHistoricoRepository, PasswordEncoder passwordEncoder) {
        this.contaRepository = contaRepository;
        this.contaHistoricoRepository = contaHistoricoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void debitar(Long id, BigDecimal saldo) throws IllegalArgumentException {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

        conta.debitar(saldo);

        Conta saved = contaRepository.save(conta);

        // Registrar histórico de atualização
        ContaHistorico historico = new ContaHistorico(
                saved.getId(),
                saved.getNome(),
                saved.getSenha(),
                new Saldo(saved.getSaldoValor(),saved.getSaldoMoeda()),
                TipoOperacao.ATUALIZACAO,
                LocalDateTime.now()
        );
        contaHistoricoRepository.save(historico);
    }

    public void creditar(Long id, BigDecimal saldo) throws IllegalArgumentException {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

        conta.creditar(saldo);

        Conta saved = contaRepository.save(conta);

        // Registrar histórico de atualização
        ContaHistorico historico = new ContaHistorico(
                saved.getId(),
                saved.getNome(),
                saved.getSenha(),
                new Saldo(saved.getSaldoValor(),saved.getSaldoMoeda()),
                TipoOperacao.ATUALIZACAO,
                LocalDateTime.now()
        );
        contaHistoricoRepository.save(historico);
    }

    public void registrar(String nome, String senha) {
        String senhaEncriptada = passwordEncoder.encode(senha);
        Conta conta = new Conta(nome, senhaEncriptada);
        contaRepository.save(conta);
    }

    public void excluirConta(Long id) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            contaRepository.delete(conta);

            ContaHistorico historico = new ContaHistorico(
                conta.getId(),
                conta.getNome(),
                conta.getSenha(),
                new Saldo(conta.getSaldoValor(), conta.getSaldoMoeda()),
                TipoOperacao.EXCLUSAO,
                LocalDateTime.now()
            );
            contaHistoricoRepository.save(historico);
        }
    }

    @Transactional(readOnly = true)
    public List<Conta> consultarContas() {
        return contaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Conta> consultarConta(Long id) {
        return contaRepository.findById(id);
    }

    public void incluirConta(Conta conta) {
        Conta saved = contaRepository.save(conta);

        ContaHistorico historico = new ContaHistorico(
            saved.getId(),
            saved.getNome(),
            saved.getSenha(),
            new Saldo(saved.getSaldoValor(),saved.getSaldoMoeda()),
            TipoOperacao.CRIACAO,
            LocalDateTime.now()
        );
        contaHistoricoRepository.save(historico);
    }

    @Transactional(readOnly = true)
    public List<ContaHistorico> consultarHistorico() {
        return contaHistoricoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ContaHistorico> consultarHistoricoPorConta(Long contaId) {
        return contaHistoricoRepository.findByContaIdOrderByDataHoraDesc(contaId);
    }
}
