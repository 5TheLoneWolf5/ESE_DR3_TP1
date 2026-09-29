package org.example.conta.application;

import java.math.BigDecimal;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaRepository;
import org.example.conta.domain.SaldoInsuficienteException;
import org.example.conta.domain.value_objects.Saldo;
import org.example.conta.infrastructure.persistence.ChaveIdempotenciaRepository;
import org.example.conta.infrastructure.persistence.ContaOutboxRepository;
import org.example.conta.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ContaApplicationTests {

    @Autowired
    private CriarContaUseCase criarContaUseCase;

    @Autowired
    private DebitarContaUseCase debitarContaUseCase;

    @Autowired
    private CreditarContaUseCase creditarContaUseCase;

    @Autowired
    private RegistrarClienteUseCase registrarClienteUseCase;

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ChaveIdempotenciaRepository idempotenciaRepository;

    @Autowired
    private ContaOutboxRepository outboxRepository;

    @Test
    @DisplayName("Registrar cliente e realizar login com JWT")
    void registrarELogin() {
        Long clienteId = registrarClienteUseCase.execute("milo", "senha123");
        assertNotNull(clienteId);

        String token = loginUseCase.execute("milo", "senha123");
        assertNotNull(token);
        assertTrue(jwtTokenService.validateToken(token));
        assertEquals(clienteId, jwtTokenService.getClienteId(token));
    }

    @Test
    @DisplayName("Debitar e creditar com garantia de idempotencia")
    void idempotenciaDebitoECredito() {
        Long contaId = criarContaUseCase.execute("Conta Teste", new BigDecimal("100.00"), "BRL");

        String chaveDebito = "transf-999-debito";
        debitarContaUseCase.execute(contaId, new BigDecimal("30.00"), chaveDebito, 999L);

        Conta conta = contaRepository.findById(contaId).orElseThrow();
        assertEquals(new BigDecimal("70.00"), conta.getSaldoValor());

        // Segunda execução com a mesma chave de idempotência NÃO deve debitar novamente
        debitarContaUseCase.execute(contaId, new BigDecimal("30.00"), chaveDebito, 999L);
        conta = contaRepository.findById(contaId).orElseThrow();
        assertEquals(new BigDecimal("70.00"), conta.getSaldoValor());

        // Agora testar crédito idempotente
        String chaveCredito = "transf-999-credito";
        creditarContaUseCase.execute(contaId, new BigDecimal("50.00"), chaveCredito, 999L);
        conta = contaRepository.findById(contaId).orElseThrow();
        assertEquals(new BigDecimal("120.00"), conta.getSaldoValor());

        // Reexecutar com mesma chave de crédito não deve creditar novamente
        creditarContaUseCase.execute(contaId, new BigDecimal("50.00"), chaveCredito, 999L);
        conta = contaRepository.findById(contaId).orElseThrow();
        assertEquals(new BigDecimal("120.00"), conta.getSaldoValor());
    }

    @Test
    @DisplayName("Debito com saldo insuficiente gera erro e grava rejeicao")
    void debitoSaldoInsuficiente() {
        Long contaId = criarContaUseCase.execute("Conta Pobre", new BigDecimal("10.00"), "BRL");

        assertThrows(SaldoInsuficienteException.class, () ->
                debitarContaUseCase.execute(contaId, new BigDecimal("50.00"), "transf-888-debito", 888L));

        Conta conta = contaRepository.findById(contaId).orElseThrow();
        assertEquals(new BigDecimal("10.00"), conta.getSaldoValor());
    }
}
