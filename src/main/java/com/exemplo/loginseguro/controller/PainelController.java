package com.exemplo.loginseguro.controller;

import com.exemplo.loginseguro.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PainelController {

    @GetMapping("/painel")
    public String painel(@AuthenticationPrincipal UsuarioAutenticado usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "painel";
    }

    @GetMapping("/gerencia/relatorios")
    public String relatorios() {
        return "gerencia/relatorios";
    }
}
