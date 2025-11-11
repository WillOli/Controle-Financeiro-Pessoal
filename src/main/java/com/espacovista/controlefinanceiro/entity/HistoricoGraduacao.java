package com.espacovista.controlefinanceiro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "historico_graduacoes", indexes = {
        @Index(name = "idx_hist_aluno", columnList = "aluno_id"),
        @Index(name = "idx_hist_graduacao", columnList = "graduacao_id")
})
public class HistoricoGraduacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "graduacao_id", nullable = false)
    private Graduacao graduacao;

    @NotNull
    @Column(name = "data_graduacao", nullable = false)
    private LocalDate dataGraduacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    public Graduacao getGraduacao() { return graduacao; }
    public void setGraduacao(Graduacao graduacao) { this.graduacao = graduacao; }
    public LocalDate getDataGraduacao() { return dataGraduacao; }
    public void setDataGraduacao(LocalDate dataGraduacao) { this.dataGraduacao = dataGraduacao; }
}