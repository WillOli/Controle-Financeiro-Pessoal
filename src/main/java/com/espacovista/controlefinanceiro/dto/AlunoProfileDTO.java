package com.espacovista.controlefinanceiro.dto;

import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import java.time.LocalDate;
import java.util.List;

public class AlunoProfileDTO {
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

    public List<HistoricoItem> historicoGraduacoes;
    public List<MensalidadeItem> mensalidades;

    public static class HistoricoItem {
        public Long graduacaoId;
        public String graduacaoNome;
        public LocalDate dataGraduacao;
    }

    public static class MensalidadeItem {
        public Integer ano;
        public Integer mes;
        public String statusPagamento;
        public LocalDate dataPagamento;
        public java.math.BigDecimal valorPago;
    }
}