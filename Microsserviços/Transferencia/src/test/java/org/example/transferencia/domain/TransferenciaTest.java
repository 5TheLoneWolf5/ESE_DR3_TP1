package org.example.transferencia.domain;

import java.math.BigDecimal;
import org.example.transferencia.domain.events.CreditoConfirmado;
import org.example.transferencia.domain.events.DebitoConfirmado;
import org.example.transferencia.domain.events.TransferenciaIniciada;
import org.example.transferencia.domain.value_objects.Dinheiro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferenciaTest {

    @Test
    @DisplayName("Nao permite transferencia entre a mesma conta")
    void naoPermiteMesmaConta() {
        Dinheiro d = new Dinheiro(new BigDecimal("100.00"), "BRL");
        assertThrows(IllegalArgumentException.class, () -> new Transferencia(1L, 1L, d));
    }

    @Test
    @DisplayName("Nao permite transferencia com valor zero ou negativo")
    void naoPermiteValorInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transferencia(1L, 2L, new Dinheiro(BigDecimal.ZERO, "BRL")));
        assertThrows(IllegalArgumentException.class, () ->
                new Transferencia(1L, 2L, new Dinheiro(new BigDecimal("-10.00"), "BRL")));
    }

    @Test
    @DisplayName("Cria transferencia com status INICIADA e gera evento TransferenciaIniciada")
    void criaTransferenciaIniciada() {
        Dinheiro d = new Dinheiro(new BigDecimal("50.00"), "BRL");
        Transferencia t = new Transferencia(1L, 2L, d);

        assertEquals(StatusTransferencia.INICIADA, t.getStatus());
        assertEquals(1, t.eventosNaoPublicados().size());
        assertTrue(t.eventosNaoPublicados().get(0) instanceof TransferenciaIniciada);
    }

    @Test
    @DisplayName("Fluxo de sucesso: INICIADA -> CONTA_ORIGEM_DEBITADA -> CONCLUIDA")
    void fluxoSucesso() {
        Dinheiro d = new Dinheiro(new BigDecimal("50.00"), "BRL");
        Transferencia t = new Transferencia(1L, 2L, d);
        t.limparEventos();

        t.confirmarDebito();
        assertEquals(StatusTransferencia.CONTA_ORIGEM_DEBITADA, t.getStatus());
        assertEquals(1, t.eventosNaoPublicados().size());
        assertTrue(t.eventosNaoPublicados().get(0) instanceof DebitoConfirmado);

        t.confirmarCredito();
        assertEquals(StatusTransferencia.CONCLUIDA, t.getStatus());
        assertEquals(2, t.eventosNaoPublicados().size());
        assertTrue(t.eventosNaoPublicados().get(1) instanceof CreditoConfirmado);
    }

    @Test
    @DisplayName("Fluxo de compensacao: INICIADA -> DEBITADA -> CREDITO_FALHOU -> COMPENSADA")
    void fluxoCompensacao() {
        Dinheiro d = new Dinheiro(new BigDecimal("50.00"), "BRL");
        Transferencia t = new Transferencia(1L, 2L, d);

        t.confirmarDebito();
        t.falharCredito("Conta destino bloqueada");
        assertEquals(StatusTransferencia.CREDITO_FALHOU, t.getStatus());

        t.confirmarCompensacao();
        assertEquals(StatusTransferencia.COMPENSADA, t.getStatus());
    }

    @Test
    @DisplayName("Transicoes invalidas lancam IllegalStateException")
    void transicoesInvalidas() {
        Dinheiro d = new Dinheiro(new BigDecimal("50.00"), "BRL");
        Transferencia t = new Transferencia(1L, 2L, d);

        assertThrows(IllegalStateException.class, t::confirmarCredito);
        assertThrows(IllegalStateException.class, t::confirmarCompensacao);
    }
}
