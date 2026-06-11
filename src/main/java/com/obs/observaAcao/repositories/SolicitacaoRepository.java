package com.obs.observaAcao.repositories;

import com.obs.observaAcao.enums.CategoriaEnum;
import com.obs.observaAcao.enums.PrioridadeEnum;
import com.obs.observaAcao.models.SolicitacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<SolicitacaoModel, Integer> {
    List<SolicitacaoModel> findByUsuarioModelId(Integer usuarioId);
    List<SolicitacaoModel> findByPrioridade(PrioridadeEnum prioridade);
    List<SolicitacaoModel> findByBairroIgnoreCase(String bairro);
    List<SolicitacaoModel> findByCategoria(CategoriaEnum categoria);
}
