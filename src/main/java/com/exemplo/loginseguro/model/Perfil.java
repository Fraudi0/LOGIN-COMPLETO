package com.exemplo.loginseguro.model;

public enum Perfil {

    ADMIN("Administrador"),
    GERENTE("Gerente"),
    USUARIO("Usuario");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
