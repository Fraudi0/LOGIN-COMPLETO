package com.exemplo.loginseguro.controller;

import com.exemplo.loginseguro.model.Perfil;
import com.exemplo.loginseguro.security.UsuarioAutenticado;
import com.exemplo.loginseguro.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("perfis", Perfil.values());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/perfil")
    public String alterarPerfil(@PathVariable String id,
                                @RequestParam Perfil perfil,
                                RedirectAttributes atributos) {
        usuarioService.alterarPerfil(id, perfil);
        atributos.addFlashAttribute("sucesso", "Perfil atualizado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/status")
    public String alternarStatus(@PathVariable String id,
                                 @AuthenticationPrincipal UsuarioAutenticado logado,
                                 RedirectAttributes atributos) {
        if (id.equals(logado.getId())) {
            atributos.addFlashAttribute("erro", "Voce nao pode desativar a propria conta.");
            return "redirect:/admin/usuarios";
        }
        usuarioService.alternarStatus(id);
        atributos.addFlashAttribute("sucesso", "Situacao do usuario atualizada.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluir(@PathVariable String id,
                          @AuthenticationPrincipal UsuarioAutenticado logado,
                          RedirectAttributes atributos) {
        if (id.equals(logado.getId())) {
            atributos.addFlashAttribute("erro", "Voce nao pode excluir a propria conta.");
            return "redirect:/admin/usuarios";
        }
        usuarioService.excluir(id);
        atributos.addFlashAttribute("sucesso", "Usuario removido.");
        return "redirect:/admin/usuarios";
    }
}
