package org.example.conta.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaContaRepositoryAdapter implements ContaRepository {

    private final SpringDataContaRepository springDataContaRepository;

    public JpaContaRepositoryAdapter(SpringDataContaRepository springDataContaRepository) {
        this.springDataContaRepository = springDataContaRepository;
    }

    @Override
    public Conta save(Conta conta) {
        return springDataContaRepository.save(conta);
    }

    @Override
    public Optional<Conta> findById(Long id) {
        return springDataContaRepository.findById(id);
    }

    @Override
    public List<Conta> findAll() {
        return springDataContaRepository.findAll();
    }

    @Override
    public void delete(Conta conta) {
        springDataContaRepository.delete(conta);
    }

    @Override
    public void deleteAll() {
        springDataContaRepository.deleteAll();
    }
}
