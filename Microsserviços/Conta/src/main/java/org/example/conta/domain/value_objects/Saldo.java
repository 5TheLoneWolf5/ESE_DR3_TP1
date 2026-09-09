package org.example.conta.domain.value_objects;

import java.math.BigDecimal;
import java.util.Objects;

public record Saldo(BigDecimal valor, String moeda) {
    public Saldo {
        Objects.requireNonNull(moeda);
    }
}
