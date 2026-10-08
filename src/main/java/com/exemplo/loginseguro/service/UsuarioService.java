package com.exemplo.loginseguro.service;

import com.exemplo.loginseguro.dto.CadastroForm;
import com.exemplo.loginseguro.model.Perfil;
import com.exemplo.loginseguro.model.Usuario;
import com.exemplo.loginseguro.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(CadastroForm form) {
        String email = form.getEmail().trim().toLowerCase();

        if (repository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        usuario.setPerfis(List.of(Perfil.USUARIO));

        return repository.save(usuario);
    }

    public Usuario criarComPerfil(String nome, String email, String senha, Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email.trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfis(List.of(perfil));
        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario nao encontrado"));
    }

    public void alterarPerfil(String id, Perfil perfil) {
        Usuario usuario = buscarPorId(id);
        usuario.setPerfis(List.of(perfil));
        repository.save(usuario);
    }

    public void alternarStatus(String id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(!usuario.isAtivo());
        repository.save(usuario);
    }

    public void excluir(String id) {
        repository.deleteById(id);
    }

    public long contar() {
        return repository.count();
    }
}
