package org.example.conta.domain;

import java.util.List;
import java.util.Optional;

public interface ContaRepository {
    Conta save(Conta conta);
    Optional<Conta> findById(Long id);
    List<Conta> findAll();
    void delete(Conta conta);
    void deleteAll();
}
