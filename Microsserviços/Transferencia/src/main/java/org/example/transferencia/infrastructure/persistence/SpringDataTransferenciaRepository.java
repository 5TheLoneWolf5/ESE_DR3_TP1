package org.example.transferencia.infrastructure.persistence;

import java.util.List;
import org.example.transferencia.domain.StatusTransferencia;
import org.example.transferencia.domain.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataTransferenciaRepository extends JpaRepository<Transferencia, Long> {
    List<Transferencia> findByStatus(StatusTransferencia status);
}
