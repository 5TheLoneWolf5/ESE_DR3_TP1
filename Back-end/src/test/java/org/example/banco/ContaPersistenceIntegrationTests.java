package org.example.banco;

import org.example.banco.entity.Conta;
import org.example.banco.entity.ContaHistorico;
import org.example.banco.entity.TipoOperacao;
import org.example.banco.repository.ContaRepository;
import org.example.banco.repository.ContaHistoricoRepository;
import org.example.banco.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
        Conta conta = new Conta("Alice", 1000.0);
        contaService.incluirContaDb(conta);

        List<Conta> contas = contaService.consultarContasDb();
        assertEquals(1, contas.size());
        assertEquals("Alice", contas.get(0).getNome());
        assertEquals(1000.0, contas.get(0).getSaldo());

        List<ContaHistorico> historicos = contaService.consultarHistoricoDb();
        assertEquals(1, historicos.size());
        
        ContaHistorico hist = historicos.get(0);
        assertEquals(contas.get(0).getId(), hist.getContaId());
        assertEquals("Alice", hist.getNome());
        assertEquals(1000.0, hist.getSaldo());
        assertEquals(TipoOperacao.CRIACAO, hist.getTipoOperacao());
        assertNotNull(hist.getDataHora());
    }

    @Test
    void testAlterarSaldoEVerificarHistorico() {
        Conta conta = new Conta("Bob", 500.0);
        contaService.incluirContaDb(conta);

        Long id = contaService.consultarContasDb().get(0).getId();

        contaService.alterarSaldoConta(id, 750.0);

        Conta atualizada = contaService.consultarContaDb(id).orElseThrow();
        assertEquals(750.0, atualizada.getSaldo());

        List<ContaHistorico> historicos = contaService.consultarHistoricoPorContaDb(id);
        assertEquals(2, historicos.size());

        assertEquals(TipoOperacao.ATUALIZACAO, historicos.get(0).getTipoOperacao());
        assertEquals(750.0, historicos.get(0).getSaldo());

        assertEquals(TipoOperacao.CRIACAO, historicos.get(1).getTipoOperacao());
        assertEquals(500.0, historicos.get(1).getSaldo());
    }

    @Test
    void testExcluirContaEVerificarHistorico() {
        Conta conta = new Conta("Charlie", 300.0);
        contaService.incluirContaDb(conta);

        Long id = contaService.consultarContasDb().get(0).getId();

        contaService.excluirContaDb(id);

        Optional<Conta> deletada = contaService.consultarContaDb(id);
        assertTrue(deletada.isEmpty());

        List<ContaHistorico> historicos = contaService.consultarHistoricoPorContaDb(id);
        assertEquals(2, historicos.size());

        assertEquals(TipoOperacao.EXCLUSAO, historicos.get(0).getTipoOperacao());
        assertEquals("Charlie", historicos.get(0).getNome());
        assertEquals(300.0, historicos.get(0).getSaldo());
    }
}
