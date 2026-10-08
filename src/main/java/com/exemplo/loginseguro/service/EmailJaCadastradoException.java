package com.exemplo.loginseguro.service;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("Ja existe um usuario cadastrado com o e-mail " + email);
    }
}
