package com.obs.observaAcao.repositories;

import com.obs.observaAcao.models.HistoricoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoRepository extends JpaRepository<HistoricoModel, Integer> {
    List<HistoricoModel> findBySolicitacaoModelProtocoloOrderByDataMovimentacaoAsc(Integer protocolo);
}
