package com.obs.observaAcao.controllers;

import com.obs.observaAcao.dtos.LoginRequestDTO;
import com.obs.observaAcao.dtos.LoginResponseDTO;
import com.obs.observaAcao.dtos.RecuperarSenhaDTO;
import com.obs.observaAcao.models.UsuarioModel;
import com.obs.observaAcao.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    /**
     * POST /auth/login
     * Retorna JWT único para o usuário autenticado.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        var credentials = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        var auth = authenticationManager.authenticate(credentials);
        UsuarioModel usuario = (UsuarioModel) auth.getPrincipal();
        String token = tokenService.gerarToken(usuario);
        return ResponseEntity.ok(new LoginResponseDTO(
                usuario.getId(), token, usuario.getTipo().name(), usuario.getNome()));
    }

    /**
     * POST /auth/recuperar-senha (público)
     * Stub seguro: responde 200 sempre, sem revelar se o e-mail existe.
     * TODO produção: integrar envio de e-mail com token de redefinição.
     */
    @PostMapping("/recuperar-senha")
    public ResponseEntity<java.util.Map<String, String>> recuperarSenha(
            @RequestBody RecuperarSenhaDTO dto) {
        return ResponseEntity.ok(java.util.Map.of(
                "message",
                "Se o e-mail informado estiver cadastrado, um link de redefinição será enviado."
        ));
    }
}
