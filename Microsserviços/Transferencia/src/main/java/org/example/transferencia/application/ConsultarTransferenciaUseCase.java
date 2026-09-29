package org.example.transferencia.application;

import java.util.List;
import java.util.Optional;
import org.example.transferencia.domain.Transferencia;
import org.example.transferencia.domain.TransferenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarTransferenciaUseCase {

    private final TransferenciaRepository repository;

    public ConsultarTransferenciaUseCase(TransferenciaRepository repository) {
        this.repository = repository;
    }

    public Optional<Transferencia> porId(Long id) {
        return repository.findById(id);
    }

    public List<Transferencia> todas() {
        return repository.findAll();
    }
}
