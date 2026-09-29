package org.example.transferencia.domain;

import java.util.List;
import java.util.Optional;

public interface TransferenciaRepository {
    Transferencia save(Transferencia transferencia);
    Optional<Transferencia> findById(Long id);
    List<Transferencia> findAll();
    List<Transferencia> findByStatus(StatusTransferencia status);
}
