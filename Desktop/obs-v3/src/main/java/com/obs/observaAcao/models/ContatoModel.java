package com.obs.observaAcao.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_contatos")
@Data
public class ContatoModel {

    @Id
    private Integer id = 1;

    private String email;
    private String telefone;

    @Column(length = 500)
    private String endereco;
}
