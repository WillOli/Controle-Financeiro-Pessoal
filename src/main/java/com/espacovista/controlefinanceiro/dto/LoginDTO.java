package com.espacovista.controlefinanceiro.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank public String username;
    @NotBlank public String password;
}