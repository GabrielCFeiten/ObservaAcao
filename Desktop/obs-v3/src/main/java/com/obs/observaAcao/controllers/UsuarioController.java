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

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody CadastroUsuarioDTO dto) {
        UsuarioModel criado = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.from(criado));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> eu(@AuthenticationPrincipal UsuarioModel logado) {
        return ResponseEntity.ok(UsuarioResponseDTO.from(logado));
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfil(
            @AuthenticationPrincipal UsuarioModel logado,
            @RequestBody AtualizarPerfilDTO dto) {
        UsuarioModel atualizado = usuarioService.atualizarPerfil(logado, dto);
        return ResponseEntity.ok(UsuarioResponseDTO.from(atualizado));
    }

    @GetMapping("/gestores")
    public List<UsuarioResponseDTO> listarGestores() {
        return usuarioService.listarGestores().stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuarioService.findById(id)));
    }

    @PatchMapping("/{id}/tipo")
    public ResponseEntity<UsuarioResponseDTO> alterarTipo(@PathVariable Integer id,
                                                          @RequestBody AlterarTipoDTO dto) {
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuarioService.alterarTipo(id, dto)));
    }
}
