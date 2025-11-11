package com.espacovista.controlefinanceiro.repository;

import com.espacovista.controlefinanceiro.entity.Mensalidade;
import com.espacovista.controlefinanceiro.entity.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    Optional<Mensalidade> findByAlunoIdAndAnoAndMes(Long alunoId, Integer ano, Integer mes);

    List<Mensalidade> findByAlunoIdOrderByAnoDescMesDesc(Long alunoId);

    @Query("SELECT m FROM Mensalidade m WHERE m.ano = :ano AND m.mes = :mes AND m.statusPagamento = :status")
    List<Mensalidade> findByAnoMesAndStatus(@Param("ano") Integer ano, @Param("mes") Integer mes, @Param("status") StatusPagamento status);
}