package org.example.banco.service;

import org.example.banco.repository.ContaRepository;
import org.example.banco.repository.ContaHistoricoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.banco.entity.Conta;
import org.example.banco.entity.ContaHistorico;
import org.example.banco.entity.TipoOperacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Fail early aplicado no back-end e fail gracefully no front-end (com mensagens claras e seguras).

@Service
@Transactional
public class ContaService {
    private final ContaRepository contaRepository;
    private final ContaHistoricoRepository contaHistoricoRepository;

    public ContaService(ContaRepository contaRepository, ContaHistoricoRepository contaHistoricoRepository) {
        this.contaRepository = contaRepository;
        this.contaHistoricoRepository = contaHistoricoRepository;
    }

    public void alterarSaldoConta(Long id, Double saldo) throws IllegalArgumentException {
    	if (saldo > 0) {
    		Conta conta = contaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));
            conta.setSaldo(saldo);
            Conta saved = contaRepository.save(conta);
            
            // Registrar histórico de atualização
            ContaHistorico historico = new ContaHistorico(
                saved.getId(),
                saved.getNome(),
                saved.getSaldo(),
                TipoOperacao.ATUALIZACAO,
                LocalDateTime.now()
            );
            contaHistoricoRepository.save(historico);
    	} else {
            // Fail early.
    		throw new IllegalArgumentException("Saldo deve ser maior que 0.");
    	}
    }

    public void excluirContaDb(Long id) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            contaRepository.delete(conta);

            ContaHistorico historico = new ContaHistorico(
                conta.getId(),
                conta.getNome(),
                conta.getSaldo(),
                TipoOperacao.EXCLUSAO,
                LocalDateTime.now()
            );
            contaHistoricoRepository.save(historico);
        }
    }

    @Transactional(readOnly = true)
    public List<Conta> consultarContasDb() {
        return contaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Conta> consultarContaDb(Long id) {
        return contaRepository.findById(id);
    }

    public void incluirContaDb(Conta conta) {
        Conta saved = contaRepository.save(conta);

        ContaHistorico historico = new ContaHistorico(
            saved.getId(),
            saved.getNome(),
            saved.getSaldo(),
            TipoOperacao.CRIACAO,
            LocalDateTime.now()
        );
        contaHistoricoRepository.save(historico);
    }

    @Transactional(readOnly = true)
    public List<ContaHistorico> consultarHistoricoDb() {
        return contaHistoricoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ContaHistorico> consultarHistoricoPorContaDb(Long contaId) {
        return contaHistoricoRepository.findByContaIdOrderByDataHoraDesc(contaId);
    }
}
