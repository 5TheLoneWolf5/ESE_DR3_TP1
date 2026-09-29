package org.example.conta.application;

import org.example.conta.domain.Cliente;
import org.example.conta.domain.ClienteRepository;
import org.example.conta.infrastructure.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginUseCase {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public LoginUseCase(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public String execute(String nome, String senhaEmTextoPuro) {
        Cliente cliente = clienteRepository.findByNome(nome)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas: cliente não encontrado."));

        if (!passwordEncoder.matches(senhaEmTextoPuro, cliente.getSenhaHash())) {
            throw new IllegalArgumentException("Credenciais inválidas: senha incorreta.");
        }

        return jwtTokenService.generateToken(cliente.getId(), cliente.getNome());
    }
}
