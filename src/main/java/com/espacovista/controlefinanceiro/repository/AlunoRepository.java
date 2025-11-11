package com.espacovista.controlefinanceiro.repository;

import com.espacovista.controlefinanceiro.entity.Aluno;
import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Query("SELECT a FROM Aluno a WHERE (:nome IS NULL OR LOWER(a.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:status IS NULL OR a.statusAluno = :status) " +
            "AND (:graduacaoId IS NULL OR a.graduacaoAtual.id = :graduacaoId)")
    Page<Aluno> search(
            @Param("nome") String nome,
            @Param("status") StatusAluno status,
            @Param("graduacaoId") Long graduacaoId,
            Pageable pageable
    );
}