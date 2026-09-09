package org.example.conta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.example.conta.application.ContaService;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaHistorico;
import org.example.conta.domain.ContaRepository;
import org.example.conta.domain.ContaHistoricoRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.StringArbitrary;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.lifecycle.BeforeTry;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTests {

	@Mock
	private ContaRepository contaRepository;

	@Mock
	private ContaHistoricoRepository contaHistoricoRepository;

	@InjectMocks
	private ContaService contaService;

	@BeforeTry
	void initMocks() {
	    MockitoAnnotations.openMocks(this);
	}

	@Property
	void consultarContaDeveRetornarContaCorreta(@ForAll @LongRange(min = 0) long id, @ForAll("contas") Conta conta) {
		when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
		assertNotNull(contaService.consultarConta(id));
		assertEquals(contaService.consultarConta(id), Optional.of(new Conta(conta.getNome(), "teste")));
	}

	@Property
	void incluirNovaContaDevePersistirNoBanco(@ForAll @LongRange(min = 0) long id, @ForAll("contas") Conta conta) {
		conta.setId(id);
		when(contaRepository.save(conta)).thenReturn(conta);
		
		contaService.incluirConta(conta);
		verify(contaRepository, times(1)).save(conta);
		verify(contaHistoricoRepository, times(1)).save(any(ContaHistorico.class));

		when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
		assertEquals(contaService.consultarConta(id), Optional.of(new Conta(conta.getNome(), "teste")));
	}

	@Property
	void excluirContaDeveApagarRegistro() {
		long id = 1;
		Conta conta = new Conta("John", "teste");
		conta.setId(id);
		when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
		
		contaService.excluirConta(id);
		verify(contaRepository, times(1)).delete(conta);
		verify(contaHistoricoRepository, times(1)).save(any(ContaHistorico.class));

		when(contaRepository.findById(id)).thenReturn(Optional.empty());
		assertFalse(contaService.consultarConta(id).isPresent(), "Retorno dever ser vazio");
	}

	@Property
	void debitarContaDeveMudarDado(@ForAll @LongRange(min = 0) long id) {
		Conta contaASerAlterada = new Conta("Adam", "teste");
		contaASerAlterada.setId(id);
		when(contaRepository.findById(id)).thenReturn(Optional.of(contaASerAlterada));
		when(contaRepository.save(contaASerAlterada)).thenReturn(contaASerAlterada);

		BigDecimal valorDebito = BigDecimal.valueOf(200);

		contaService.debitar(id, valorDebito);

		assertEquals(BigDecimal.valueOf(1200), contaASerAlterada.getSaldoValor());
		verify(contaRepository, times(1)).save(contaASerAlterada);
		verify(contaHistoricoRepository, times(1)).save(any(ContaHistorico.class));
	}

	@Property
	void creditarContaDeveMudarDado(@ForAll @LongRange(min = 0) long id) {
		Conta contaASerAlterada = new Conta("Adam", "teste");
		contaASerAlterada.setId(id);
		when(contaRepository.findById(id)).thenReturn(Optional.of(contaASerAlterada));
		when(contaRepository.save(contaASerAlterada)).thenReturn(contaASerAlterada);

		BigDecimal valorCredito = BigDecimal.valueOf(200);

		contaService.creditar(id, valorCredito);

		assertEquals(BigDecimal.valueOf(1600), contaASerAlterada.getSaldoValor());
		verify(contaRepository, times(1)).save(contaASerAlterada);
		verify(contaHistoricoRepository, times(1)).save(any(ContaHistorico.class));
	}

	@Property
	void tentarDebitarComValorNegativoOuZeroDeveJogarExcecao() {
		assertThrows(IllegalArgumentException.class, () -> contaService.debitar(3L, BigDecimal.valueOf(-100)));
		assertThrows(IllegalArgumentException.class, () -> contaService.debitar(3L, BigDecimal.ZERO));
	}

	@Property
	void tentarCreditarComValorNegativoOuZeroDeveJogarExcecao() {
		assertThrows(IllegalArgumentException.class, () -> contaService.creditar(3L, BigDecimal.valueOf(-100)));
		assertThrows(IllegalArgumentException.class, () -> contaService.creditar(3L, BigDecimal.ZERO));
	}

	@Property
	void consultarContasDeveRetornarZeroOuMaisContas(@ForAll("listaDeContas") List<Conta> contas) {
		System.out.println(contas);
		when(contaRepository.findAll()).thenReturn(contas);
		assertTrue(contaService.consultarContas().size() > -1);
	}

	@Provide
	Arbitrary<Conta> contas() {
		StringArbitrary nomes = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(255);
        StringArbitrary senhas = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(255);
//		BigDecimalArbitrary saldos = Arbitraries.bigDecimals().greaterThan(BigDecimal.ZERO);
//		Arbitrary<String> moedas = Arbitraries.of("BRL", "USD", "EUR");
//		Arbitrary<Saldo> saldoObjects = Combinators.combine(saldos, moedas).as(Saldo::new);

		return Combinators.combine(nomes, senhas).as(Conta::new);
	}

	@Provide
	Arbitrary<List<Conta>> listaDeContas() {
		return contas().list().ofMinSize(0).ofMaxSize(20);
	}

}
