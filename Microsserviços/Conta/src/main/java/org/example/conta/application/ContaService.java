package org.example.conta.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaHistorico;
import org.example.conta.domain.ContaHistoricoRepository;
import org.example.conta.domain.ContaRepository;
import org.example.conta.domain.TipoOperacao;
import org.example.conta.domain.value_objects.Saldo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContaService {

    private final ContaRepository contaRepository;
    private final ContaHistoricoRepository contaHistoricoRepository;
    private final DebitarContaUseCase debitarContaUseCase;
    private final CreditarContaUseCase creditarContaUseCase;
    private final CriarContaUseCase criarContaUseCase;
    private final ConsultarContaUseCase consultarContaUseCase;

    public ContaService(
            ContaRepository contaRepository,
            ContaHistoricoRepository contaHistoricoRepository,
            DebitarContaUseCase debitarContaUseCase,
            CreditarContaUseCase creditarContaUseCase,
            CriarContaUseCase criarContaUseCase,
            ConsultarContaUseCase consultarContaUseCase
    ) {
        this.contaRepository = contaRepository;
        this.contaHistoricoRepository = contaHistoricoRepository;
        this.debitarContaUseCase = debitarContaUseCase;
        this.creditarContaUseCase = creditarContaUseCase;
        this.criarContaUseCase = criarContaUseCase;
        this.consultarContaUseCase = consultarContaUseCase;
    }

    public void debitar(Long id, BigDecimal saldo) {
        debitarContaUseCase.execute(id, saldo, null, null);
        Conta saved = contaRepository.findById(id).orElse(null);
        if (saved != null) {
            ContaHistorico historico = new ContaHistorico(
                    saved.getId(),
                    saved.getNome(),
                    "",
                    new Saldo(saved.getSaldoValor(), saved.getSaldoMoeda()),
                    TipoOperacao.ATUALIZACAO,
                    LocalDateTime.now()
            );
            contaHistoricoRepository.save(historico);
        }
    }

    public void creditar(Long id, BigDecimal saldo) {
        creditarContaUseCase.execute(id, saldo, null, null);
        Conta saved = contaRepository.findById(id).orElse(null);
        if (saved != null) {
            ContaHistorico historico = new ContaHistorico(
                    saved.getId(),
                    saved.getNome(),
                    "",
                    new Saldo(saved.getSaldoValor(), saved.getSaldoMoeda()),
                    TipoOperacao.ATUALIZACAO,
                    LocalDateTime.now()
            );
            contaHistoricoRepository.save(historico);
        }
    }

    public void excluirConta(Long id) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            contaRepository.delete(conta);

            ContaHistorico historico = new ContaHistorico(
                    conta.getId(),
                    conta.getNome(),
                    "",
                    new Saldo(conta.getSaldoValor(), conta.getSaldoMoeda()),
                    TipoOperacao.EXCLUSAO,
                    LocalDateTime.now()
            );
            contaHistoricoRepository.save(historico);
        }
    }

    @Transactional(readOnly = true)
    public List<Conta> consultarContas() {
        return consultarContaUseCase.todas();
    }

    @Transactional(readOnly = true)
    public Optional<Conta> consultarConta(Long id) {
        return consultarContaUseCase.porId(id);
    }

    public void incluirConta(Conta conta) {
        Conta saved = contaRepository.save(conta);

        ContaHistorico historico = new ContaHistorico(
                saved.getId(),
                saved.getNome(),
                "",
                new Saldo(saved.getSaldoValor(), saved.getSaldoMoeda()),
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
