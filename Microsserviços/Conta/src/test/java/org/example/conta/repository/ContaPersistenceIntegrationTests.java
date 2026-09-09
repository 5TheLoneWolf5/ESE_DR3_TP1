package org.example.conta.repository;

import org.example.conta.CrudT1Application;
import org.example.conta.domain.*;
import org.example.conta.application.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CrudT1Application.class)
@ActiveProfiles("test")
public class ContaPersistenceIntegrationTests {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ContaHistoricoRepository contaHistoricoRepository;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        contaHistoricoRepository.deleteAll();
    }

    @Test
    void testCriarContaEVerificarHistorico() {
        Conta conta = new Conta("Alice", "teste");
        contaService.incluirConta(conta);

        List<Conta> contas = contaService.consultarContas();
        assertEquals(1, contas.size());
        assertEquals("Alice", contas.get(0).getNome());
        assertEquals(new BigDecimal("1000.00"), contas.get(0).getSaldoValor());
        assertEquals("BRL", contas.get(0).getSaldoMoeda());

        List<ContaHistorico> historicos = contaService.consultarHistorico();
        assertEquals(1, historicos.size());
        
        ContaHistorico hist = historicos.get(0);
        assertEquals(contas.get(0).getId(), hist.getContaId());
        assertEquals("Alice", hist.getNome());
        assertEquals(new BigDecimal("1000.00"), hist.getSaldoValor());
        assertEquals("BRL", hist.getSaldoMoeda());
        assertEquals(TipoOperacao.CRIACAO, hist.getTipoOperacao());
        assertNotNull(hist.getDataHora());
    }

    @Test
    void testAlterarSaldoEVerificarHistorico() {
        Conta conta = new Conta("Bob", "teste");
        contaService.incluirConta(conta);

        Long id = contaService.consultarContas().get(0).getId();

        contaService.creditar(id, new BigDecimal("250.00"));

        Conta atualizada = contaService.consultarConta(id).orElseThrow();
        assertEquals(new BigDecimal("750.00"), atualizada.getSaldoValor());

        List<ContaHistorico> historicos = contaService.consultarHistoricoPorConta(id);
        assertEquals(2, historicos.size());

        assertEquals(TipoOperacao.ATUALIZACAO, historicos.get(0).getTipoOperacao());
        assertEquals(new BigDecimal("750.00"), historicos.get(0).getSaldoValor());

        assertEquals(TipoOperacao.CRIACAO, historicos.get(1).getTipoOperacao());
        assertEquals(new BigDecimal("500.00"), historicos.get(1).getSaldoValor());
    }

    @Test
    void testExcluirContaEVerificarHistorico() {
        Conta conta = new Conta("Charlie", "teste");
        contaService.incluirConta(conta);

        Long id = contaService.consultarContas().get(0).getId();

        contaService.excluirConta(id);

        Optional<Conta> deletada = contaService.consultarConta(id);
        assertTrue(deletada.isEmpty());

        List<ContaHistorico> historicos = contaService.consultarHistoricoPorConta(id);
        assertEquals(2, historicos.size());

        assertEquals(TipoOperacao.EXCLUSAO, historicos.get(0).getTipoOperacao());
        assertEquals("Charlie", historicos.get(0).getNome());
        assertEquals(new BigDecimal("300.00"), historicos.get(0).getSaldoValor());
    }
}
