package com.espacovista.controlefinanceiro.config;

import com.espacovista.controlefinanceiro.entity.Usuario;
import com.espacovista.controlefinanceiro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Cria usuário ADMIN inicial se não houver usuários
        if (usuarioRepository.count() == 0) {
            String defaultUsername = envOrDefault("ADMIN_USERNAME", "admin");
            String defaultPassword = envOrDefault("ADMIN_PASSWORD", "admin123");

            Usuario admin = new Usuario();
            admin.setUsername(defaultUsername);
            admin.setPassword(passwordEncoder.encode(defaultPassword));
            admin.setRole("ADMIN");
            usuarioRepository.save(admin);

            System.out.println("[Init] Usuário ADMIN inicial criado: " + defaultUsername);
        }
    }

    private String envOrDefault(String key, String def) {
        String val = System.getenv(key);
        return val != null && !val.isBlank() ? val : def;
    }
}