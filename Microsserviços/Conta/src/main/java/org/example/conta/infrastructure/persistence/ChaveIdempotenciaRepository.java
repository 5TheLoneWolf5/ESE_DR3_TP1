package org.example.conta.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChaveIdempotenciaRepository extends JpaRepository<ChaveIdempotencia, String> {
}
