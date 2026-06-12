package com.obs.observaAcao.dtos;

import com.obs.observaAcao.enums.CategoriaEnum;

public record DenunciaAnonimaDTO(
    CategoriaEnum categoria,
    String descricao,
    String bairro,
    String endereco
) {}
