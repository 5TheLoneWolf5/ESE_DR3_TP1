package org.example.conta.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaOutboxRepository extends JpaRepository<ContaOutboxMessage, Long> {
    List<ContaOutboxMessage> findByPublicadoFalseOrderByCriadoEmAsc();
}
