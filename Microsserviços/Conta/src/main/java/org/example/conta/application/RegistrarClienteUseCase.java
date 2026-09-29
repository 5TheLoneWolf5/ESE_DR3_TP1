package org.example.conta.application;

import org.example.conta.domain.Cliente;
import org.example.conta.domain.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrarClienteUseCase(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long execute(String nome, String senhaEmTextoPuro) {
        if (clienteRepository.findByNome(nome).isPresent()) {
            throw new IllegalArgumentException("Cliente já cadastrado com o nome: " + nome);
        }
        String hash = passwordEncoder.encode(senhaEmTextoPuro);
        Cliente cliente = new Cliente(nome, hash);
        Cliente salvo = clienteRepository.save(cliente);
        return salvo.getId();
    }
}
