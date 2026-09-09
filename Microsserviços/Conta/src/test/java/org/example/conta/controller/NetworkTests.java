package org.example.conta.controller;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.conta.domain.Conta;
import org.example.conta.application.ContaService;
import org.example.conta.web.ContaController;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
public class NetworkTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaService contaService;

    @Autowired
    private ObjectMapper objectMapper;

    @FuzzTest
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
    void fuzzAdicionarConta(FuzzedDataProvider data) {
        Conta conta = new Conta(data.consumeString(50), data.consumeString(50));

        Mockito.doNothing().when(contaService).incluirConta(conta);

        try {
            mockMvc.perform(post("/contas-banco/adicionar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(conta)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Conta criada com sucesso!"));
        } catch (JsonProcessingException e) {
            System.out.println("Erro de processamento do JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
