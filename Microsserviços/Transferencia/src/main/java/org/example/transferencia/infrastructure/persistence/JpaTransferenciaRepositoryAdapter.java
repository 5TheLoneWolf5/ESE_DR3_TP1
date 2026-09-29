package org.example.transferencia.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.example.transferencia.domain.StatusTransferencia;
import org.example.transferencia.domain.Transferencia;
import org.example.transferencia.domain.TransferenciaRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaTransferenciaRepositoryAdapter implements TransferenciaRepository {

    private final SpringDataTransferenciaRepository springDataRepository;

    public JpaTransferenciaRepositoryAdapter(SpringDataTransferenciaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Transferencia save(Transferencia transferencia) {
        return springDataRepository.save(transferencia);
    }

    @Override
    public Optional<Transferencia> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public List<Transferencia> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public List<Transferencia> findByStatus(StatusTransferencia status) {
        return springDataRepository.findByStatus(status);
    }
}
