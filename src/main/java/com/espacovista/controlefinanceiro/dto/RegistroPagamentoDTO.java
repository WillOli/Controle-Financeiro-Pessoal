package com.espacovista.controlefinanceiro.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroPagamentoDTO {
    @NotNull public Long alunoId;
    @NotNull public Integer ano;
    @NotNull public Integer mes;
    @NotNull public BigDecimal valorPago;
    public LocalDate dataPagamento; // opcional, padrão hoje
}