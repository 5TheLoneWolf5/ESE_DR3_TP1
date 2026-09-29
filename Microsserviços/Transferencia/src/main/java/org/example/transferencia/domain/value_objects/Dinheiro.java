package org.example.transferencia.domain.value_objects;

import java.math.BigDecimal;
import java.util.Objects;

public record Dinheiro(BigDecimal valor, String moeda) {
    public Dinheiro {
        Objects.requireNonNull(valor, "valor não pode ser nulo");
        if (moeda == null || moeda.isBlank()) {
            throw new IllegalArgumentException("moeda não pode ser vazia");
        }
    }
}
