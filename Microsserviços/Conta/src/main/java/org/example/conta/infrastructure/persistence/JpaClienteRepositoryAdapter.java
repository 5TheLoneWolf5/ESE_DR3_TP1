package org.example.conta.infrastructure.persistence;

import java.util.Optional;
import org.example.conta.domain.Cliente;
import org.example.conta.domain.ClienteRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaClienteRepositoryAdapter implements ClienteRepository {

    private final SpringDataClienteRepository springDataClienteRepository;

    public JpaClienteRepositoryAdapter(SpringDataClienteRepository springDataClienteRepository) {
        this.springDataClienteRepository = springDataClienteRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        return springDataClienteRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return springDataClienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> findByNome(String nome) {
        return springDataClienteRepository.findByNome(nome);
    }
}
