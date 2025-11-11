package com.espacovista.controlefinanceiro.dto;

import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AlunoCreateDTO {
    @NotBlank public String nomeCompleto;
    public String fotoUrl;
    @NotNull public LocalDate dataNascimento;
    public String cpfRg;
    @NotBlank public String telefone;
    public String email;
    public String endereco;
    public String contatoEmergenciaNome;
    public String contatoEmergenciaTelefone;
    public String restricoesMedicas;
    public LocalDate dataInicio;
    public StatusAluno statusAluno; // opcional, padrão ATIVO
    public Long graduacaoAtualId; // opcional no cadastro
}