package com.obs.observaAcao.controllers;

import com.obs.observaAcao.dtos.DenunciaAnonimaDTO;
import com.obs.observaAcao.enums.CategoriaEnum;
import com.obs.observaAcao.enums.PrioridadeEnum;
import com.obs.observaAcao.enums.StatusEnum;
import com.obs.observaAcao.models.HistoricoModel;
import com.obs.observaAcao.models.SolicitacaoModel;
import com.obs.observaAcao.models.UsuarioModel;
import com.obs.observaAcao.services.SolicitacaoService;
import com.obs.observaAcao.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<SolicitacaoModel> buscarTodas() {
        return solicitacaoService.findAll();
    }

    @GetMapping("/minhas")
    public List<SolicitacaoModel> buscarMinhas(@AuthenticationPrincipal UsuarioModel usuarioLogado) {
        return solicitacaoService.findByUsuario(usuarioLogado.getId());
    }

    @GetMapping("/movimentadas-por-mim")
    public List<SolicitacaoModel> buscarMovimentadasPorMim(@AuthenticationPrincipal UsuarioModel gestorLogado) {
        return solicitacaoService.findMovimentadasPorGestor(gestorLogado.getId());
    }

    @GetMapping("/{protocolo}")
    public SolicitacaoModel buscarPorProtocolo(@PathVariable Integer protocolo) {
        return solicitacaoService.findById(protocolo);
    }

    @GetMapping("/{protocolo}/historico")
    public List<HistoricoModel> buscarHistorico(@PathVariable Integer protocolo) {
        return solicitacaoService.findHistorico(protocolo);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<SolicitacaoModel> buscarPorUsuario(@PathVariable Integer usuarioId) {
        return solicitacaoService.findByUsuario(usuarioId);
    }

    @GetMapping("/filtro/prioridade")
    public List<SolicitacaoModel> buscarPorPrioridade(@RequestParam PrioridadeEnum prioridade) {
        return solicitacaoService.findByPrioridade(prioridade);
    }

    @GetMapping("/filtro/bairro")
    public List<SolicitacaoModel> buscarPorBairro(@RequestParam String bairro) {
        return solicitacaoService.findByBairro(bairro);
    }

    @GetMapping("/filtro/categoria")
    public List<SolicitacaoModel> buscarPorCategoria(@RequestParam CategoriaEnum categoria) {
        return solicitacaoService.findByCategoria(categoria);
    }

    @PostMapping
    public ResponseEntity<SolicitacaoModel> salvar(
            @RequestBody SolicitacaoModel solicitacaoModel,
            @AuthenticationPrincipal UsuarioModel usuarioLogado) {
        SolicitacaoModel salva = solicitacaoService.save(solicitacaoModel, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PostMapping("/anonima")
    public ResponseEntity<SolicitacaoModel> denunciarAnonimo(@RequestBody DenunciaAnonimaDTO dto) {
        SolicitacaoModel salva = solicitacaoService.salvarDenunciaAnonima(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PatchMapping("/{protocolo}/status")
    public SolicitacaoModel atualizarStatus(
            @PathVariable Integer protocolo,
            @RequestParam StatusEnum novoStatus,
            @RequestParam String justificativa,
            @AuthenticationPrincipal UsuarioModel gestorLogado) {
        return solicitacaoService.atualizarStatus(protocolo, novoStatus, justificativa, gestorLogado);
    }
}
