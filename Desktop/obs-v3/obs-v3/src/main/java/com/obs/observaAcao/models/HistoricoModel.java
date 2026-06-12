package com.obs.observaAcao.models;

import com.obs.observaAcao.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historicos")
@Data
public class HistoricoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private LocalDateTime dataMovimentacao = LocalDateTime.now();
    private String justificativa;

    @ManyToOne
    @JoinColumn(name = "solicitacao_id")
    private SolicitacaoModel solicitacaoModel;
    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = true)
    private UsuarioModel responsavel;
}
