package org.example.banco;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.example.banco.entity.Conta;
import org.example.banco.repository.ContaRepository;
import org.example.banco.service.ContaService;
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
import net.jqwik.api.arbitraries.DoubleArbitrary;
import net.jqwik.api.arbitraries.StringArbitrary;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.lifecycle.BeforeTry;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTests {

	@Mock
	private ContaRepository contaRepository;

	@InjectMocks
	private ContaService contaService;

	@BeforeTry
	void initMocks() {
	    MockitoAnnotations.openMocks(this);
	}

	/**
	 * O override do método equals() de Conta faz com que dois objetos Conta com os mesmos valores sejam considerados iguais pois tem os mesmos campos.
	 */

	@Property
	void consultarContaDeveRetornarContaCorreta(@ForAll @LongRange(min = 0) long id, @ForAll("contas") Conta conta) {
		when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
		assertNotNull(contaService.consultarContaDb(id));
		assertEquals(contaService.consultarContaDb(id), Optional.of(new Conta(conta.getNome(), conta.getSaldo())));
	}

	@Property
	void incluirNovaContaDevePersistirNoBanco(@ForAll @LongRange(min = 0) long id, @ForAll("contas") Conta conta) {
		contaService.incluirContaDb(conta);
		verify(contaRepository, times(1)).save(conta);

		when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
		assertEquals(contaService.consultarContaDb(id), Optional.of(new Conta(conta.getNome(), conta.getSaldo())));
	}

	@Property
	void excluirContaDeveApagarRegistro() {
		long id = 1;
		contaService.excluirContaDb(id);
		verify(contaRepository, times(1)).deleteById(id);

		when(contaRepository.findById(id)).thenReturn(Optional.empty());
		assertFalse(contaService.consultarContaDb(id).isPresent(), "Retorno dever ser vazio");
	}

	@Property
	void alterarContaDeveMudarDado(@ForAll @LongRange(min = 0) long id) {
		Conta contaASerAlterada = new Conta("Adam", 1400D);
		when(contaRepository.findById(id)).thenReturn(Optional.of(contaASerAlterada));

		double novoSaldo = 200D;

		contaService.alterarSaldoConta(id, novoSaldo);

		assertEquals(novoSaldo, contaASerAlterada.getSaldo());
		verify(contaRepository, times(1)).save(contaASerAlterada);
	}

	@Property
	void tentarAlterarSaldoComValorNegativoDeveJogarExcecao(@ForAll @DoubleRange(min = -100, max = -1) double novoSaldo) {
		assertThrows(IllegalArgumentException.class, () -> contaService.alterarSaldoConta(3L, novoSaldo));
	}

	@Property
	void consultarContasDeveRetornarZeroOuMaisContas(@ForAll("listaDeContas") List<Conta> contas) {
		System.out.println(contas);
		when(contaRepository.findAll()).thenReturn((contas));
		assertTrue(contaService.consultarContasDb().size() > -1);
	}

	@Provide
	Arbitrary<Conta> contas() {
		StringArbitrary nomes = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(255);
		DoubleArbitrary saldos = Arbitraries.doubles().greaterThan(0);

		return Combinators.combine(nomes, saldos).as(Conta::new);
	}

	@Provide
	Arbitrary<List<Conta>> listaDeContas() {
		return contas().list().ofMinSize(0).ofMaxSize(20);
	}

}
