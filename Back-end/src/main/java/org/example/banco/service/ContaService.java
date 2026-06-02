package org.example.banco.service;

import org.example.banco.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.example.banco.entity.Conta;
import java.util.List;
import java.util.Optional;

// Fail early aplicado no back-end e fail gracefully no front-end (com mensagens claras e seguras).

@Service
public class ContaService {
    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public void alterarSaldoConta(Long id, Double saldo) throws IllegalArgumentException {
    	if (saldo > 0) {
    		Conta conta = contaRepository.findById(id).get();
            conta.setSaldo(saldo);
            contaRepository.save(conta);
    	} else {
            // Fail early.
    		throw new IllegalArgumentException("Saldo deve ser maior que 0.");
    	}
    }

    public void excluirContaDb(Long id) {
        contaRepository.deleteById(id);
    }

    public List<Conta> consultarContasDb() {
        return contaRepository.findAll();
    }

    public Optional<Conta> consultarContaDb(Long id) {
        return contaRepository.findById(id);
    }

    public void incluirContaDb(Conta conta) {
        contaRepository.save(conta);
    }
}
