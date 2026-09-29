package org.example.conta.domain.value_objects;

import java.math.BigDecimal;
import java.util.Objects;

public record Saldo(BigDecimal valor, String moeda) {
    public Saldo {
        Objects.requireNonNull(valor, "valor não pode ser nulo");
        if (moeda == null || moeda.isBlank()) {
            throw new IllegalArgumentException("moeda não pode ser vazia");
        }
    }
}
