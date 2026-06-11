package com.obs.observaAcao.dev;

import com.obs.observaAcao.enums.CategoriaEnum;
import com.obs.observaAcao.enums.StatusEnum;
import com.obs.observaAcao.enums.TipoUsuarioEnum;
import com.obs.observaAcao.models.SolicitacaoModel;
import com.obs.observaAcao.models.UsuarioModel;
import com.obs.observaAcao.repositories.UsuarioRepository;
import com.obs.observaAcao.services.ContatoService;
import com.obs.observaAcao.services.SolicitacaoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * =====================================================================
 *  DEVELOPMENT ONLY — popula o banco com usuários e dados de demonstração.
 *  Ativado apenas com o profile "dev" (spring.profiles.active=dev).
 *  NÃO é carregado em produção. Para remover: apague o pacote `dev`.
 * =====================================================================
 *
 *  Usuários de demonstração criados:
 *    - GESTOR:  admin@observa.com    / admin123
 *    - CIDADAO: cidadao@observa.com  / cidadao123
 */
@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SolicitacaoService solicitacaoService;
    private final ContatoService contatoService;

    public DevDataSeeder(UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder,
                         SolicitacaoService solicitacaoService,
                         ContatoService contatoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.solicitacaoService = solicitacaoService;
        this.contatoService = contatoService;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByLogin("admin@observa.com")) {
            return; // já populado
        }

        UsuarioModel gestor = criarUsuario(
                "Ana Gestora", "111.111.111-11", "(44) 99999-0001",
                "admin@observa.com", "admin123", TipoUsuarioEnum.GESTOR);

        UsuarioModel cidadao = criarUsuario(
                "Carlos Cidadão", "222.222.222-22", "(44) 99999-0002",
                "cidadao@observa.com", "cidadao123", TipoUsuarioEnum.CIDADAO);

        // Solicitações de exemplo (prioridade/prazo são derivados da categoria)
        seedSolicitacao(CategoriaEnum.DENUNCIAR_ESGOTO_CEU_ABERTO,
                "Esgoto correndo a céu aberto próximo à escola municipal.",
                "Jardim Independência", "Rua das Acácias, 120", cidadao, gestor, StatusEnum.ABERTO);

        seedSolicitacao(CategoriaEnum.DENUNCIAR_LIXO_ACUMULADO,
                "Acúmulo de lixo em terreno baldio há mais de duas semanas.",
                "Vila Esperança", "Av. Brasil, 980", cidadao, gestor, StatusEnum.TRIAGEM);

        seedSolicitacao(CategoriaEnum.SOLICITAR_VISTORIA_SANITARIA,
                "Solicito vistoria sanitária para abertura de lanchonete.",
                "Centro", "Rua XV de Novembro, 45", cidadao, gestor, StatusEnum.EM_EXECUCAO);

        seedSolicitacao(CategoriaEnum.DENUNCIAR_AGUA_PARADA,
                "Piscina abandonada com água parada, risco de dengue.",
                "Parque Itália", "Rua Minas Gerais, 333", cidadao, gestor, StatusEnum.RESOLVIDO);

        seedSolicitacao(CategoriaEnum.SOLICITAR_COLETA_RESIDUOS_ESPECIAIS,
                "Preciso de coleta de resíduos eletrônicos descartados.",
                "Jardim Universo", "Rua Paraná, 77", cidadao, gestor, StatusEnum.ABERTO);

        contatoService.obter(); // garante o registro de contato padrão

        System.out.println("[DEV] Dados de demonstração criados. " +
                "Login gestor: admin@observa.com / admin123");
    }

    private UsuarioModel criarUsuario(String nome, String cpf, String tel,
                                      String login, String senha, TipoUsuarioEnum tipo) {
        UsuarioModel u = new UsuarioModel();
        u.setNome(nome);
        u.setCpf(cpf);
        u.setTelefone(tel);
        u.setLogin(login);
        u.setPassword(passwordEncoder.encode(senha));
        u.setTipo(tipo);
        return usuarioRepository.save(u);
    }

    private void seedSolicitacao(CategoriaEnum categoria, String descricao, String bairro,
                                 String endereco, UsuarioModel autor, UsuarioModel gestor,
                                 StatusEnum status) {
        SolicitacaoModel s = new SolicitacaoModel();
        s.setCategoria(categoria);
        s.setDescricao(descricao);
        s.setBairro(bairro);
        s.setEndereco(endereco);
        SolicitacaoModel salva = solicitacaoService.save(s, autor);
        // Varia o status usando o caminho real de gestor (gera histórico coerente)
        if (status != StatusEnum.ABERTO) {
            solicitacaoService.atualizarStatus(
                    salva.getProtocolo(), status, "Movimentação inicial (dados de demo)", gestor);
        }
    }
}
