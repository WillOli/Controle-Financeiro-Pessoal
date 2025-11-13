package com.espacovista.controlefinanceiro.controller;

import com.espacovista.controlefinanceiro.security.SecurityConfig;
import com.espacovista.controlefinanceiro.security.TestSecurityConfig;
import com.espacovista.controlefinanceiro.entity.Usuario;
import com.espacovista.controlefinanceiro.repository.UsuarioRepository;
import com.espacovista.controlefinanceiro.security.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    AuthenticationConfiguration authenticationConfiguration;
    @MockBean
    JwtTokenService jwtTokenService;
    @MockBean
    UsuarioRepository usuarioRepository;
    @MockBean
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupAuthManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
    }

    @Test
    @DisplayName("POST /auth/login com credenciais corretas retorna 200 e token")
    void login_ok_retornandoToken() throws Exception {
        UserDetails principal = User.withUsername("user").password("pass").roles("INSTRUTOR").build();
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
        when(jwtTokenService.generateToken(any(UserDetails.class))).thenReturn("abc.jwt.token");

        String body = "{\"username\":\"user\",\"password\":\"pass\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("abc.jwt.token"));
    }

    @Test
    @DisplayName("POST /auth/login com senha errada retorna 401")
    void login_credenciaisInvalidas_401() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        String body = "{\"username\":\"user\",\"password\":\"wrong\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/register sem autenticação retorna 401")
    void register_semAuth_401() throws Exception {
        String body = "{\"username\":\"novo\",\"password\":\"senha\",\"role\":\"INSTRUTOR\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INSTRUTOR")
    @DisplayName("POST /auth/register com INSTRUTOR retorna 403")
    void register_instrutor_403() throws Exception {
        String body = "{\"username\":\"novo\",\"password\":\"senha\",\"role\":\"INSTRUTOR\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /auth/register com ADMIN retorna 200")
    void register_admin_200() throws Exception {
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        String body = "{\"username\":\"novo\",\"password\":\"senha\",\"role\":\"INSTRUTOR\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
