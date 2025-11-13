package com.espacovista.controlefinanceiro.controller;

import com.espacovista.controlefinanceiro.security.SecurityConfig;
import com.espacovista.controlefinanceiro.security.TestSecurityConfig;
import com.espacovista.controlefinanceiro.dto.MensalidadeStatusDTO;
import com.espacovista.controlefinanceiro.service.MensalidadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MensalidadeController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class MensalidadeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MensalidadeService mensalidadeService;
    @MockBean
    AuthenticationConfiguration authenticationConfiguration;
    @MockBean
    org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @BeforeEach
    void setupAuthManager() throws Exception {
        org.mockito.Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
    }

    @Test
    @DisplayName("POST /mensalidades/pagar sem auth retorna 401")
    void pagar_semAuth_401() throws Exception {
        String body = "{\"alunoId\":1,\"ano\":2024,\"mes\":11,\"valorPago\":100.0}";
        mockMvc.perform(post("/mensalidades/pagar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INSTRUTOR")
    @DisplayName("POST /mensalidades/pagar com INSTRUTOR retorna 403")
    void pagar_instrutor_403() throws Exception {
        String body = "{\"alunoId\":1,\"ano\":2024,\"mes\":11,\"valorPago\":100.0}";
        mockMvc.perform(post("/mensalidades/pagar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /mensalidades/pagar com ADMIN retorna 200")
    void pagar_admin_200() throws Exception {
        MensalidadeStatusDTO dto = new MensalidadeStatusDTO();
        dto.id = 1L;
        dto.alunoId = 1L;
        dto.ano = 2024;
        dto.mes = 11;
        dto.statusPagamento = "PAGO";
        when(mensalidadeService.registrarPagamento(any())).thenReturn(dto);

        String body = "{\"alunoId\":1,\"ano\":2024,\"mes\":11,\"valorPago\":100.0}";
        mockMvc.perform(post("/mensalidades/pagar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusPagamento").value("PAGO"));
    }

    @Test
    @DisplayName("GET /mensalidades/pendentes sem auth retorna 401")
    void pendentes_semAuth_401() throws Exception {
        mockMvc.perform(get("/mensalidades/pendentes").param("ano", "2024").param("mes", "11"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INSTRUTOR")
    @DisplayName("GET /mensalidades/pendentes com INSTRUTOR retorna 403")
    void pendentes_instrutor_403() throws Exception {
        mockMvc.perform(get("/mensalidades/pendentes").param("ano", "2024").param("mes", "11"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /mensalidades/pendentes com ADMIN retorna 200 e lista")
    void pendentes_admin_200() throws Exception {
        MensalidadeStatusDTO dto = new MensalidadeStatusDTO();
        dto.id = 1L; dto.alunoId = 1L; dto.ano = 2024; dto.mes = 11; dto.statusPagamento = "PENDENTE";
        when(mensalidadeService.buscarPendentes(anyInt(), anyInt())).thenReturn(List.of(dto));

        mockMvc.perform(get("/mensalidades/pendentes").param("ano", "2024").param("mes", "11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusPagamento").value("PENDENTE"));
    }

    @Test
    @DisplayName("PATCH /mensalidades/{id}/status sem auth retorna 401")
    void alterarStatus_semAuth_401() throws Exception {
        mockMvc.perform(patch("/mensalidades/1/status").param("status", "PENDENTE"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INSTRUTOR")
    @DisplayName("PATCH /mensalidades/{id}/status com INSTRUTOR retorna 403")
    void alterarStatus_instrutor_403() throws Exception {
        mockMvc.perform(patch("/mensalidades/1/status").param("status", "PENDENTE"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /mensalidades/{id}/status com ADMIN retorna 200")
    void alterarStatus_admin_200() throws Exception {
        MensalidadeStatusDTO dto = new MensalidadeStatusDTO();
        dto.id = 1L; dto.alunoId = 1L; dto.ano = 2024; dto.mes = 11; dto.statusPagamento = "PENDENTE";
        when(mensalidadeService.alterarStatus(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(patch("/mensalidades/1/status").param("status", "PENDENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusPagamento").value("PENDENTE"));
    }
}
