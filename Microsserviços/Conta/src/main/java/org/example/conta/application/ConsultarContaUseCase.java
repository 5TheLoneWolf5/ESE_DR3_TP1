package org.example.conta.application;

import java.util.List;
import java.util.Optional;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarContaUseCase {

    private final ContaRepository contaRepository;

    public ConsultarContaUseCase(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public Optional<Conta> porId(Long id) {
        return contaRepository.findById(id);
    }

    public List<Conta> todas() {
        return contaRepository.findAll();
    }
}
