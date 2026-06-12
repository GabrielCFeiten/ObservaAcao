package com.obs.observaAcao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.obs.observaAcao.enums.CategoriaEnum;
import com.obs.observaAcao.enums.PrioridadeEnum;
import com.obs.observaAcao.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_solicitacoes")
@Data
public class SolicitacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer protocolo;

    @Enumerated(EnumType.STRING)
    private CategoriaEnum categoria;

    private String descricao;
    private String bairro;
    private String endereco;

    @Enumerated(EnumType.STRING)
    private PrioridadeEnum prioridade;

    @Enumerated(EnumType.STRING)
    private StatusEnum status = StatusEnum.ABERTO;

    private LocalDate prazo;

    private LocalDate dataCriacao = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private UsuarioModel usuarioModel;

    @OneToMany(mappedBy = "solicitacaoModel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HistoricoModel> historicoList = new ArrayList<>();
}
