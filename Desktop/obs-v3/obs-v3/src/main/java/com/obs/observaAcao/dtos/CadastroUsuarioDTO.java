package com.obs.observaAcao.dtos;

public record CadastroUsuarioDTO(
    String nome,
    String cpf,
    String telefone,
    String login,
    String password
) {}
