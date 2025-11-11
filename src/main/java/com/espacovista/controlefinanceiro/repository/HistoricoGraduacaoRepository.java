package com.espacovista.controlefinanceiro.repository;

import com.espacovista.controlefinanceiro.entity.HistoricoGraduacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoGraduacaoRepository extends JpaRepository<HistoricoGraduacao, Long> {
    List<HistoricoGraduacao> findByAlunoIdOrderByDataGraduacaoDesc(Long alunoId);
}