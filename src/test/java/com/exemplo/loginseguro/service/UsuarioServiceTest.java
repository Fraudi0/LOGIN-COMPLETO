package com.exemplo.loginseguro.service;

import com.exemplo.loginseguro.dto.CadastroForm;
import com.exemplo.loginseguro.model.Perfil;
import com.exemplo.loginseguro.model.Usuario;
import com.exemplo.loginseguro.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    private UsuarioRepository repository;
    private UsuarioService service;
    private PasswordEncoder encoder;

    @BeforeEach
    void preparar() {
        repository = mock(UsuarioRepository.class);
        encoder = new BCryptPasswordEncoder(4);
        service = new UsuarioService(repository, encoder);
        when(repository.save(any(Usuario.class))).thenAnswer(chamada -> chamada.getArgument(0));
    }

    @Test
    void deveSalvarASenhaCriptografada() {
        when(repository.existsByEmail(anyString())).thenReturn(false);

        Usuario salvo = service.cadastrar(formulario("Maria", "Maria@Exemplo.com", "senhaForte1"));

        assertNotEquals("senhaForte1", salvo.getSenha());
        assertTrue(encoder.matches("senhaForte1", salvo.getSenha()));
    }

    @Test
    void deveGuardarOEmailEmMinusculoComPerfilPadrao() {
        when(repository.existsByEmail(anyString())).thenReturn(false);

        Usuario salvo = service.cadastrar(formulario("Joao", "JOAO@Exemplo.com", "senhaForte1"));

        assertEquals("joao@exemplo.com", salvo.getEmail());
        assertEquals(Perfil.USUARIO, salvo.getPerfis().get(0));
    }

    @Test
    void naoDeveAceitarEmailRepetido() {
        when(repository.existsByEmail("maria@exemplo.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class,
                () -> service.cadastrar(formulario("Maria", "maria@exemplo.com", "senhaForte1")));
    }

    @Test
    void deveAlternarASituacaoDoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setAtivo(true);
        when(repository.findById("1")).thenReturn(Optional.of(usuario));

        service.alternarStatus("1");

        assertFalse(usuario.isAtivo());
    }

    private CadastroForm formulario(String nome, String email, String senha) {
        CadastroForm form = new CadastroForm();
        form.setNome(nome);
        form.setEmail(email);
        form.setSenha(senha);
        form.setConfirmacaoSenha(senha);
        return form;
    }
}
