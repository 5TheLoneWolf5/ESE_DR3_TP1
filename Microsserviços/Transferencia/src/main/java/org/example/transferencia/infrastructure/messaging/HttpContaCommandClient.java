package org.example.transferencia.infrastructure.messaging;

import java.math.BigDecimal;
import org.example.transferencia.application.ContaCommandClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpContaCommandClient implements ContaCommandClient {

    private static final Logger log = LoggerFactory.getLogger(HttpContaCommandClient.class);

    private final RestTemplate restTemplate;
    private final String contaServiceUrl;

    public HttpContaCommandClient(
            @Value("${conta.service.url:http://localhost:8081}") String contaServiceUrl
    ) {
        this.restTemplate = new RestTemplate();
        this.contaServiceUrl = contaServiceUrl;
    }

    @Override
    public void debitar(Long contaId, BigDecimal valor, String chaveIdempotencia, Long transferenciaId) {
        String url = String.format("%s/contas-banco/debitar/%d/%s", contaServiceUrl, contaId, valor.toPlainString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Idempotency-Key", chaveIdempotencia);
        if (transferenciaId != null) {
            headers.set("X-Correlation-Id", transferenciaId.toString());
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.info("Enviando comando debitar para Conta: url={}, transferenciaId={}, chaveIdempotencia={}",
                url, transferenciaId, chaveIdempotencia);
        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            log.error("Erro ao chamar debitar em Conta: {}", e.getMessage());
            throw new RuntimeException("Falha na chamada de debito: " + e.getMessage(), e);
        }
    }

    @Override
    public void creditar(Long contaId, BigDecimal valor, String chaveIdempotencia, Long transferenciaId) {
        String url = String.format("%s/contas-banco/creditar/%d/%s", contaServiceUrl, contaId, valor.toPlainString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Idempotency-Key", chaveIdempotencia);
        if (transferenciaId != null) {
            headers.set("X-Correlation-Id", transferenciaId.toString());
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.info("Enviando comando creditar para Conta: url={}, transferenciaId={}, chaveIdempotencia={}",
                url, transferenciaId, chaveIdempotencia);
        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            log.error("Erro ao chamar creditar em Conta: {}", e.getMessage());
            throw new RuntimeException("Falha na chamada de credito: " + e.getMessage(), e);
        }
    }
}
