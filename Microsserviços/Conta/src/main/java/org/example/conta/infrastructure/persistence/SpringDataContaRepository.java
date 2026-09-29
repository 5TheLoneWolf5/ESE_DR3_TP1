package org.example.conta.infrastructure.persistence;

import org.example.conta.domain.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataContaRepository extends JpaRepository<Conta, Long> {
}
