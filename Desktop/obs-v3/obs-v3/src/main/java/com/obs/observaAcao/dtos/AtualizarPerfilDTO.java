package com.obs.observaAcao.dtos;

/** Edição do próprio perfil. Campos nulos/vazios são ignorados (mantêm o valor atual). */
public record AtualizarPerfilDTO(
        String nome,
        String telefone,
        String login,
        String password
) {}
