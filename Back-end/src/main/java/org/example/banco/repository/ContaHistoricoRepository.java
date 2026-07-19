package org.example.banco.repository;

import org.example.banco.entity.ContaHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaHistoricoRepository extends JpaRepository<ContaHistorico, Long> {
    List<ContaHistorico> findByContaIdOrderByDataHoraDesc(Long contaId);
}
