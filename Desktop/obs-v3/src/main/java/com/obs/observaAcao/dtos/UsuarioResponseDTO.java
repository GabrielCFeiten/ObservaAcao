package com.obs.observaAcao.dtos;

import com.obs.observaAcao.models.UsuarioModel;

public record UsuarioResponseDTO(
        Integer id,
        String nome,
        String cpf,
        String telefone,
        String login,
        String tipo
) {
    public static UsuarioResponseDTO from(UsuarioModel u) {
        return new UsuarioResponseDTO(
                u.getId(), u.getNome(), u.getCpf(), u.getTelefone(),
                u.getLogin(), u.getTipo() != null ? u.getTipo().name() : null
        );
    }
}
