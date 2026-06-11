package com.obs.observaAcao.services;

import com.obs.observaAcao.enums.CategoriaEnum;
import com.obs.observaAcao.dtos.DenunciaAnonimaDTO;
import com.obs.observaAcao.enums.PrioridadeEnum;
import com.obs.observaAcao.enums.StatusEnum;
import com.obs.observaAcao.enums.TipoUsuarioEnum;
import com.obs.observaAcao.models.HistoricoModel;
import com.obs.observaAcao.models.SolicitacaoModel;
import com.obs.observaAcao.models.UsuarioModel;
import com.obs.observaAcao.repositories.HistoricoRepository;
import com.obs.observaAcao.repositories.SolicitacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    public List<SolicitacaoModel> findAll() {
        return solicitacaoRepository.findAll();
    }

    public SolicitacaoModel findById(Integer protocolo) {
        return solicitacaoRepository.findById(protocolo)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado: " + protocolo));
    }

    public List<SolicitacaoModel> findByUsuario(Integer usuarioId) {
        return solicitacaoRepository.findByUsuarioModelId(usuarioId);
    }

    public List<SolicitacaoModel> findByPrioridade(PrioridadeEnum prioridade) {
        return solicitacaoRepository.findByPrioridade(prioridade);
    }

    public List<SolicitacaoModel> findByBairro(String bairro) {
        return solicitacaoRepository.findByBairroIgnoreCase(bairro);
    }

    public List<SolicitacaoModel> findByCategoria(CategoriaEnum categoria) {
        return solicitacaoRepository.findByCategoria(categoria);
    }

    /** Salvar solicitação de usuário autenticado (CIDADAO ou GESTOR) */
    public SolicitacaoModel save(SolicitacaoModel solicitacaoModel, UsuarioModel usuario) {
        validarDescricao(solicitacaoModel.getDescricao());

        solicitacaoModel.setUsuarioModel(usuario);
        solicitacaoModel.setPrioridade(solicitacaoModel.getCategoria().getPrioridade());
        solicitacaoModel.setPrazo(calcularPrazo(solicitacaoModel.getPrioridade()));
        solicitacaoModel.setStatus(StatusEnum.ABERTO);

        SolicitacaoModel salva = solicitacaoRepository.save(solicitacaoModel);
        registrarHistorico(salva, StatusEnum.ABERTO, "Solicitação criada", usuario);
        return salva;
    }

    /** Denúncia anônima — sem necessidade de usuário autenticado */
    public SolicitacaoModel salvarDenunciaAnonima(DenunciaAnonimaDTO dto) {
        if (!dto.categoria().isPermiteAnonimo()) {
            throw new RuntimeException("Essa categoria não permite denúncia anônima.");
        }
        validarDescricao(dto.descricao());

        SolicitacaoModel solicitacao = new SolicitacaoModel();
        solicitacao.setCategoria(dto.categoria());
        solicitacao.setDescricao(dto.descricao());
        solicitacao.setBairro(dto.bairro());
        solicitacao.setEndereco(dto.endereco());
        solicitacao.setPrioridade(dto.categoria().getPrioridade());
        solicitacao.setPrazo(calcularPrazo(dto.categoria().getPrioridade()));
        solicitacao.setStatus(StatusEnum.ABERTO);
        solicitacao.setUsuarioModel(null); // anônimo: sem vínculo

        SolicitacaoModel salva = solicitacaoRepository.save(solicitacao);
        registrarHistorico(salva, StatusEnum.ABERTO, "Denúncia anônima recebida", null);
        return salva;
    }

    /** Apenas GESTOR pode atualizar status */
    public SolicitacaoModel atualizarStatus(Integer protocolo, StatusEnum novoStatus,
                                             String justificativa, UsuarioModel gestor) {
        if (gestor.getTipo() != TipoUsuarioEnum.GESTOR) {
            throw new RuntimeException("Apenas gestores podem atualizar o status.");
        }
        SolicitacaoModel solicitacao = findById(protocolo);
        solicitacao.setStatus(novoStatus);
        solicitacaoRepository.save(solicitacao);
        registrarHistorico(solicitacao, novoStatus, justificativa, gestor);
        return solicitacao;
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().length() < 10) {
            throw new RuntimeException("Descrição deve ter no mínimo 10 caracteres.");
        }
    }

    private void registrarHistorico(SolicitacaoModel solicitacao, StatusEnum status,
                                     String justificativa, UsuarioModel responsavel) {
        HistoricoModel historico = new HistoricoModel();
        historico.setSolicitacaoModel(solicitacao);
        historico.setStatus(status);
        historico.setJustificativa(justificativa);
        historico.setResponsavel(responsavel); // pode ser null para anônimo
        historicoRepository.save(historico);
    }

    private LocalDate calcularPrazo(PrioridadeEnum prioridade) {
        return switch (prioridade) {
            case ALTA  -> LocalDate.now().plusDays(1);
            case MEDIA -> LocalDate.now().plusDays(3);
            case BAIXA -> LocalDate.now().plusDays(7);
        };
    }
}
