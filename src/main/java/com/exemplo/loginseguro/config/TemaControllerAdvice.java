package com.exemplo.loginseguro.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class TemaControllerAdvice {

    private final TemaProperties tema;

    public TemaControllerAdvice(TemaProperties tema) {
        this.tema = tema;
    }

    @ModelAttribute("tema")
    public TemaProperties tema() {
        return tema;
    }
}
