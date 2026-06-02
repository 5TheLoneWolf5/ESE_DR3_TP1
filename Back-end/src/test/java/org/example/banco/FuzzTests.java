package org.example.banco;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;
import net.jqwik.api.lifecycle.BeforeTry;
import org.example.banco.entity.Conta;
import org.example.banco.repository.ContaRepository;
import org.example.banco.service.ContaService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuzzTests {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    @BeforeTry
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @FuzzTest
    void fuzzCriarEBuscarContaCriada(@NotNull String nome, @NotNull Double saldo, @NotNull Long id) {
        Conta conta = new Conta(nome, saldo);

        contaService.incluirContaDb(conta);
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertEquals(contaService.consultarContaDb(id), Optional.of(conta));
    }

    @FuzzTest
    void fuzzDeletarConta(FuzzedDataProvider data) {
        Long id = data.consumeLong();
        contaService.excluirContaDb(id);
    }

}