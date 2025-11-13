package com.espacovista.controlefinanceiro.security;

import com.espacovista.controlefinanceiro.entity.Usuario;
import com.espacovista.controlefinanceiro.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_adminDeveTerRoleAdmin() {
        Usuario u = new Usuario();
        u.setUsername("admin");
        u.setPassword("hash");
        u.setRole("ADMIN");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(u));

        UserDetails details = userDetailsService.loadUserByUsername("admin");
        Assertions.assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_quandoNaoEncontradoLancaExcecao() {
        when(usuarioRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("ghost"));
    }
}