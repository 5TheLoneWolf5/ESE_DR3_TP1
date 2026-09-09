package org.example.conta.service;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;
import net.jqwik.api.lifecycle.BeforeTry;
import org.example.conta.application.ContaService;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaHistoricoRepository;
import org.example.conta.domain.ContaRepository;
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

    @Mock
    private ContaHistoricoRepository contaHistoricoRepository;

    @InjectMocks
    private ContaService contaService;

    @BeforeTry
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @FuzzTest
    void fuzzCriarEBuscarContaCriada(@NotNull String nome, @NotNull String senha, @NotNull Long id) {
        Conta conta = new Conta(nome, senha);
        conta.setId(id);
        when(contaRepository.save(conta)).thenReturn(conta);

        contaService.incluirConta(conta);
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertEquals(contaService.consultarConta(id), Optional.of(conta));
    }

    @FuzzTest
    void fuzzDeletarConta(FuzzedDataProvider data) {
        Long id = data.consumeLong();
        contaService.excluirConta(id);
    }

}
