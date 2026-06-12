package com.obs.observaAcao.repositories;

import com.obs.observaAcao.models.HistoricoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoricoRepository extends JpaRepository<HistoricoModel, Integer> {
    List<HistoricoModel> findBySolicitacaoModelProtocoloOrderByDataMovimentacaoAsc(Integer protocolo);

    @Query("SELECT DISTINCT h.solicitacaoModel FROM HistoricoModel h WHERE h.responsavel.id = :responsavelId ORDER BY h.solicitacaoModel.protocolo DESC")
    List<com.obs.observaAcao.models.SolicitacaoModel> findSolicitacoesMovimentadasPorResponsavel(@Param("responsavelId") Integer responsavelId);
}
