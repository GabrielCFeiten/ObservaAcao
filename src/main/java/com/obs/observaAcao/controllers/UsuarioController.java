package com.obs.observaAcao.controllers;

import com.obs.observaAcao.dtos.AlterarTipoDTO;
import com.obs.observaAcao.dtos.AtualizarPerfilDTO;
import com.obs.observaAcao.dtos.CadastroUsuarioDTO;
import com.obs.observaAcao.dtos.UsuarioResponseDTO;
import com.obs.observaAcao.models.UsuarioModel;
import com.obs.observaAcao.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * POST /usuarios/cadastro (público)
     * Qualquer pessoa pode se cadastrar. Tipo fixo: CIDADAO.
     */
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody CadastroUsuarioDTO dto) {
        UsuarioModel criado = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.from(criado));
    }

    /**
     * GET /usuarios/me (autenticado)
     * Retorna os dados do próprio usuário logado — usado pelo modal de configurações.
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> eu(@AuthenticationPrincipal UsuarioModel logado) {
        return ResponseEntity.ok(UsuarioResponseDTO.from(logado));
    }

    /**
     * PATCH /usuarios/me (autenticado)
     * Edita o próprio perfil (nome, telefone, login, senha).
     */
    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfil(
            @AuthenticationPrincipal UsuarioModel logado,
            @RequestBody AtualizarPerfilDTO dto) {
        UsuarioModel atualizado = usuarioService.atualizarPerfil(logado, dto);
        return ResponseEntity.ok(UsuarioResponseDTO.from(atualizado));
    }

    /**
     * GET /usuarios/gestores (apenas GESTOR)
     * Lista todos os administradores cadastrados.
     */
    @GetMapping("/gestores")
    public List<UsuarioResponseDTO> listarGestores() {
        return usuarioService.listarGestores().stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    /**
     * GET /usuarios/{id} (autenticado)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuarioService.findById(id)));
    }

    /**
     * PATCH /usuarios/{id}/tipo (apenas GESTOR)
     * Promove CIDADAO -> GESTOR ou rebaixa GESTOR -> CIDADAO.
     */
    @PatchMapping("/{id}/tipo")
    public ResponseEntity<UsuarioResponseDTO> alterarTipo(@PathVariable Integer id,
                                                          @RequestBody AlterarTipoDTO dto) {
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuarioService.alterarTipo(id, dto)));
    }
}
