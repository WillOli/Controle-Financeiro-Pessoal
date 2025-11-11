package com.espacovista.controlefinanceiro.entity;

import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "alunos", indexes = {
        @Index(name = "idx_aluno_nome", columnList = "nome_completo"),
        @Index(name = "idx_aluno_status", columnList = "status_aluno"),
        @Index(name = "idx_aluno_graduacao", columnList = "graduacao_atual_id")
})
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "foto_url")
    private String fotoUrl;

    @NotNull
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "cpf_rg", unique = true)
    private String cpfRg;

    @NotBlank
    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "email")
    private String email;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "contato_emergencia_nome")
    private String contatoEmergenciaNome;

    @Column(name = "contato_emergencia_telefone")
    private String contatoEmergenciaTelefone;

    @Lob
    @Column(name = "restricoes_medicas")
    private String restricoesMedicas;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aluno", nullable = false)
    private StatusAluno statusAluno = StatusAluno.ATIVO;

    @ManyToOne
    @JoinColumn(name = "graduacao_atual_id")
    private Graduacao graduacaoAtual;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCpfRg() { return cpfRg; }
    public void setCpfRg(String cpfRg) { this.cpfRg = cpfRg; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getContatoEmergenciaNome() { return contatoEmergenciaNome; }
    public void setContatoEmergenciaNome(String contatoEmergenciaNome) { this.contatoEmergenciaNome = contatoEmergenciaNome; }
    public String getContatoEmergenciaTelefone() { return contatoEmergenciaTelefone; }
    public void setContatoEmergenciaTelefone(String contatoEmergenciaTelefone) { this.contatoEmergenciaTelefone = contatoEmergenciaTelefone; }
    public String getRestricoesMedicas() { return restricoesMedicas; }
    public void setRestricoesMedicas(String restricoesMedicas) { this.restricoesMedicas = restricoesMedicas; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public StatusAluno getStatusAluno() { return statusAluno; }
    public void setStatusAluno(StatusAluno statusAluno) { this.statusAluno = statusAluno; }
    public Graduacao getGraduacaoAtual() { return graduacaoAtual; }
    public void setGraduacaoAtual(Graduacao graduacaoAtual) { this.graduacaoAtual = graduacaoAtual; }
}