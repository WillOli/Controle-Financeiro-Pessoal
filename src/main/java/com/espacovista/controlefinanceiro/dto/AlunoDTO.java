package com.espacovista.controlefinanceiro.dto;

import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import java.time.LocalDate;

public class AlunoDTO {
    public Long id;
    public String nomeCompleto;
    public String fotoUrl;
    public LocalDate dataNascimento;
    public String cpfRg;
    public String telefone;
    public String email;
    public String endereco;
    public String contatoEmergenciaNome;
    public String contatoEmergenciaTelefone;
    public String restricoesMedicas;
    public LocalDate dataInicio;
    public StatusAluno statusAluno;
    public Long graduacaoAtualId;
    public String graduacaoAtualNome;
}