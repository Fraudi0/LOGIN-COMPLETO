package com.exemplo.loginseguro.config;

import com.exemplo.loginseguro.model.Perfil;
import com.exemplo.loginseguro.repository.UsuarioRepository;
import com.exemplo.loginseguro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final UsuarioService usuarioService;

    @Value("${app.admin.email}")
    private String emailAdmin;

    @Value("${app.admin.senha}")
    private String senhaAdmin;

    public CargaInicial(UsuarioRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        String email = emailAdmin.trim().toLowerCase();

        if (repository.existsByEmail(email)) {
            return;
        }

        usuarioService.criarComPerfil("Administrador", email, senhaAdmin, Perfil.ADMIN);
        System.out.println("Usuario administrador criado: " + email);
    }
}
