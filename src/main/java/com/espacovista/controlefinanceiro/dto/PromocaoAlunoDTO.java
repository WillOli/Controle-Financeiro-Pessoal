package com.espacovista.controlefinanceiro.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PromocaoAlunoDTO {
    @NotNull public Long novaGraduacaoId;
    @NotNull public LocalDate dataGraduacao;
}