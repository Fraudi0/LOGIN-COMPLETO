package com.exemplo.loginseguro.controller;

import com.exemplo.loginseguro.dto.CadastroForm;
import com.exemplo.loginseguro.service.EmailJaCadastradoException;
import com.exemplo.loginseguro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AutenticacaoController {

    private final UsuarioService usuarioService;

    public AutenticacaoController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String formularioCadastro(Model model) {
        model.addAttribute("cadastroForm", new CadastroForm());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("cadastroForm") CadastroForm form,
                            BindingResult resultado,
                            RedirectAttributes atributos) {

        if (!form.senhasConferem()) {
            resultado.rejectValue("confirmacaoSenha", "senhas.diferentes", "As senhas nao conferem");
        }

        if (resultado.hasErrors()) {
            return "cadastro";
        }

        try {
            usuarioService.cadastrar(form);
        } catch (EmailJaCadastradoException e) {
            resultado.rejectValue("email", "email.duplicado", e.getMessage());
            return "cadastro";
        }

        atributos.addFlashAttribute("sucesso", "Cadastro realizado. Agora e so entrar.");
        return "redirect:/login";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}
