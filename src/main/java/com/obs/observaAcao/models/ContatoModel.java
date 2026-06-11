package com.obs.observaAcao.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Canais públicos de contato exibidos ao cidadão.
 * Mantemos uma única linha (id = 1) sempre presente.
 */
@Entity
@Table(name = "tb_contatos")
@Data
public class ContatoModel {

    @Id
    private Integer id = 1; // registro único

    private String email;
    private String telefone;

    @Column(length = 500)
    private String endereco;
}
