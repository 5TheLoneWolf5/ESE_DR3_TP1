package org.example.conta.web.dto;

import java.math.BigDecimal;

public record CriarContaRequest(String nome, BigDecimal saldoInicial, String moeda) {}
