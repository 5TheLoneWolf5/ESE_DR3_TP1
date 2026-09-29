package org.example.conta.domain;

import java.math.BigDecimal;
import org.example.conta.domain.events.ContaCreditada;
import org.example.conta.domain.events.ContaDebitada;
import org.example.conta.domain.value_objects.Saldo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContaDomainTest {

    @Test
    @DisplayName("Cria conta com saldo inicial e valida reconstrucao de Saldo sob demanda")
    void criaContaComSaldo() {
        Saldo saldoInicial = new Saldo(new BigDecimal("100.00"), "BRL");
        Conta conta = new Conta("Cliente A", saldoInicial);

        assertEquals("Cliente A", conta.getNome());
        assertEquals(new BigDecimal("100.00"), conta.getSaldo().valor());
        assertEquals("BRL", conta.getSaldo().moeda());
        assertEquals(new BigDecimal("100.00"), conta.getSaldoValor());
    }

    @Test
    @DisplayName("Creditar adiciona ao saldo e gera evento ContaCreditada")
    void creditarComSucesso() {
        Saldo saldoInicial = new Saldo(new BigDecimal("50.00"), "BRL");
        Conta conta = new Conta("Cliente A", saldoInicial);
        conta.limparEventos();

        conta.creditar(new BigDecimal("25.00"), "chave-credito-1", 100L);

        assertEquals(new BigDecimal("75.00"), conta.getSaldoValor());
        assertEquals(1, conta.eventosNaoPublicados().size());
        assertTrue(conta.eventosNaoPublicados().get(0) instanceof ContaCreditada);
    }

    @Test
    @DisplayName("Nao permite creditar valor zero ou negativo")
    void creditarInvalido() {
        Conta conta = new Conta("Cliente A", new Saldo(new BigDecimal("50.00"), "BRL"));
        assertThrows(IllegalArgumentException.class, () -> conta.creditar(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> conta.creditar(new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Debitar subtrai do saldo e gera evento ContaDebitada")
    void debitarComSucesso() {
        Saldo saldoInicial = new Saldo(new BigDecimal("100.00"), "BRL");
        Conta conta = new Conta("Cliente A", saldoInicial);
        conta.limparEventos();

        conta.debitar(new BigDecimal("40.00"), "chave-debito-1", 100L);

        assertEquals(new BigDecimal("60.00"), conta.getSaldoValor());
        assertEquals(1, conta.eventosNaoPublicados().size());
        assertTrue(conta.eventosNaoPublicados().get(0) instanceof ContaDebitada);
    }

    @Test
    @DisplayName("Debitar alem do saldo lanca SaldoInsuficienteException e nao muta o saldo")
    void debitarSaldoInsuficiente() {
        Saldo saldoInicial = new Saldo(new BigDecimal("50.00"), "BRL");
        Conta conta = new Conta("Cliente A", saldoInicial);

        assertThrows(SaldoInsuficienteException.class, () ->
                conta.debitar(new BigDecimal("50.01"), "chave-debito-2", 101L));
        assertEquals(new BigDecimal("50.00"), conta.getSaldoValor());
    }

    @Test
    @DisplayName("Equals e hashCode baseados exclusivamente no ID")
    void equalsBaseadoEmId() {
        Conta c1 = new Conta("Conta 1", new Saldo(new BigDecimal("10.00"), "BRL"));
        Conta c2 = new Conta("Conta 2", new Saldo(new BigDecimal("99.00"), "USD"));

        // Sem ID, duas instâncias diferentes não são iguais
        assertNotEquals(c1, c2);
    }
}
