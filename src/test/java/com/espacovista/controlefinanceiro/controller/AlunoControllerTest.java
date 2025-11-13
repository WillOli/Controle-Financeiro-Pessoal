package com.espacovista.controlefinanceiro.controller;

import com.espacovista.controlefinanceiro.security.SecurityConfig;
import com.espacovista.controlefinanceiro.security.TestSecurityConfig;
import com.espacovista.controlefinanceiro.dto.AlunoDTO;
import com.espacovista.controlefinanceiro.dto.PromocaoAlunoDTO;
import com.espacovista.controlefinanceiro.entity.enums.StatusAluno;
import com.espacovista.controlefinanceiro.service.AlunoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlunoController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class AlunoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlunoService alunoService;
    @MockBean
    AuthenticationConfiguration authenticationConfiguration;
    @MockBean
    org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @BeforeEach
    void setupAuthManager() throws Exception {
        org.mockito.Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
    }

    @Test
    @DisplayName("GET /alunos sem autenticação retorna 401")
    void getAlunos_semAuth_401() throws Exception {
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INSTRUTOR")
    @DisplayName("GET /alunos com INSTRUTOR retorna 200")
    void getAlunos_instrutor_200() throws Exception {
        when(alunoService.search(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Page.empty());
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /alunos com ADMIN retorna 200")
    void getAlunos_admin_200() throws Exception {
        when(alunoService.search(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Page.empty());
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "INSTRUTOR")
    @DisplayName("POST /alunos/{id}/promover com INSTRUTOR retorna 403")
    void promover_instrutor_403() throws Exception {
        String body = "{\"novaGraduacaoId\":1,\"dataGraduacao\":\"2024-11-05\"}";
        mockMvc.perform(post("/alunos/1/promover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /alunos/{id}/promover com ADMIN retorna 200")
    void promover_admin_200() throws Exception {
        doNothing().when(alunoService).promoverAluno(eq(1L), any(PromocaoAlunoDTO.class));
        String body = "{\"novaGraduacaoId\":1,\"dataGraduacao\":\"2024-11-05\"}";
        mockMvc.perform(post("/alunos/1/promover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "INSTRUTOR")
    @DisplayName("POST /alunos (criar) autenticado retorna 200")
    void criarAluno_auth_200() throws Exception {
        AlunoDTO dto = new AlunoDTO();
        dto.id = 10L;
        dto.nomeCompleto = "Novo Aluno";
        dto.telefone = "99999-9999";
        when(alunoService.create(any())).thenReturn(dto);

        // Campos mínimos obrigatórios: nomeCompleto, dataNascimento, telefone
        String body = "{\"nomeCompleto\":\"Novo Aluno\",\"dataNascimento\":\"2000-01-01\",\"telefone\":\"99999-9999\"}";
        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
