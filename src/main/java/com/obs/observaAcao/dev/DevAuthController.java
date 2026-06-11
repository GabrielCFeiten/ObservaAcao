package com.obs.observaAcao.dev;

import com.obs.observaAcao.dtos.LoginResponseDTO;
import com.obs.observaAcao.models.UsuarioModel;
import com.obs.observaAcao.repositories.UsuarioRepository;
import com.obs.observaAcao.services.TokenService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * =====================================================================
 *  DEVELOPMENT ONLY — atalho de autenticação para testes.
 *  Ativado apenas com o profile "dev" (spring.profiles.active=dev).
 *  Em produção este controller NÃO é registrado (não existe a rota).
 *
 *  Importante: NÃO enfraquece a segurança de produção.
 *  - Não há senha embutida no código de produção.
 *  - A rota só é liberada no SecurityConfiguration quando o profile dev
 *    está ativo (ver bloco condicional lá).
 *  Para remover antes de publicar: apague o pacote `dev` por completo.
 * =====================================================================
 *
 *  Uso:
 *    POST /auth/dev-login           -> autentica como GESTOR demo
 *    POST /auth/dev-login?perfil=cidadao -> autentica como CIDADAO demo
 */
@RestController
@RequestMapping("/auth")
@Profile("dev")
public class DevAuthController {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public DevAuthController(UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/dev-login")
    public ResponseEntity<LoginResponseDTO> devLogin(
            @RequestParam(defaultValue = "gestor") String perfil) {

        String login = perfil.equalsIgnoreCase("cidadao")
                ? "cidadao@observa.com"
                : "admin@observa.com";

        UserDetails ud = usuarioRepository.findByLogin(login);
        if (ud == null) {
            return ResponseEntity.status(503).build(); // seeder ainda não rodou
        }
        UsuarioModel usuario = (UsuarioModel) ud;
        String token = tokenService.gerarToken(usuario);
        return ResponseEntity.ok(new LoginResponseDTO(
                usuario.getId(), token, usuario.getTipo().name(), usuario.getNome()));
    }
}
