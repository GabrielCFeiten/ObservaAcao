# Relatório de correções — Observa Ação (front ↔ back)

Objetivo: garantir que o front-end funcione de ponta a ponta, sem dados
faltando, sem endpoints inexistentes e sem bloqueios de autenticação,
com acesso rápido para testes.

---

## 1. Endpoints que não existiam e foram criados

| Método | Rota | Para quê | Acesso |
|---|---|---|---|
| GET | `/usuarios/me` | Carregar o próprio perfil no modal "Configurações" | autenticado |
| PATCH | `/usuarios/me` | Editar nome/telefone/e-mail/senha do próprio usuário | autenticado |
| GET | `/usuarios/gestores` | Listar administradores na tela "Admins" | GESTOR |
| GET | `/contatos` | Ler os canais públicos exibidos ao cidadão | público |
| PUT | `/contatos` | Gestor editar e salvar os contatos | GESTOR |
| POST | `/auth/recuperar-senha` | Tela "Esqueci minha senha" | público |
| POST | `/auth/dev-login` | **DEV ONLY** — atalho de login | público (só no profile dev) |

Antes dessas adições, o front recorria a `localStorage` (contatos e lista de
admins) e mostrava mensagens de "endpoint pendente" (perfil e recuperação de
senha). Agora tudo bate em endpoints reais.

## 2. Campos que faltavam e foram adicionados

- **`id` no `LoginResponseDTO`** — o front precisava do id do usuário logado
  (para marcar "você" na lista de admins e para futuras consultas por usuário).
  Agora o login retorna `{ id, token, tipo, nome }`.
- **Entidade `ContatoModel`** (`tb_contatos`) — não existia. Campos: `id`
  (fixo = 1), `email`, `telefone`, `endereco`.
- **`UsuarioResponseDTO`** — representação do usuário **sem senha** para todas
  as respostas que devolvem dados de usuário.
- **DTOs novos**: `AtualizarPerfilDTO`, `RecuperarSenhaDTO`, `ContatoDTO`.

## 3. Bugs de backend corrigidos

1. **Vazamento de senha no JSON (crítico).** `UsuarioModel` é serializado
   dentro de cada `SolicitacaoModel` (`usuarioModel`) e era retornado inteiro
   pelo `/usuarios/cadastro`. O hash BCrypt da senha (e CPF) ia para o cliente.
   Corrigido com `@JsonIgnore` em `password` e nos getters internos do
   `UserDetails` (`getPassword`, `getUsername`, `getAuthorities`), e com o uso
   de `UsuarioResponseDTO` nas respostas dos controllers de usuário.
2. **Erros viravam HTTP 500 sem corpo.** Regras de negócio lançavam
   `RuntimeException`, devolvendo 500 e nenhuma mensagem útil. Criado
   `GlobalExceptionHandler` (`@RestControllerAdvice`) que padroniza o corpo
   `{ timestamp, status, error, message }` e mapeia:
   - validação/regra de negócio → **400**
   - "não encontrado" → **404**
   - credenciais inválidas → **401**
   - sem permissão → **403**
3. **Sem rota para servir o front.** `anyRequest().authenticated()` bloquearia
   `*.html`, `/css/**`, `/js/**`. Adicionadas regras `permitAll` para os
   estáticos, permitindo servir o front pelo próprio Spring (sem CORS).

## 4. Divergências front ↔ back resolvidas

- **Nomenclatura de severidade.** O front fala "urgentes / médios / leves";
  o enum é `ALTA / MEDIA / BAIXA`. Mapeado no front (`LABELS.prioridade`),
  sem alterar o backend. Documentado para evitar confusão.
- **"comentário" vs `justificativa`.** O painel de gestão chama de
  "comentário"; o `PATCH /solicitacoes/{protocolo}/status` espera
  `justificativa` (query param). O front envia no nome correto.
- **Listagem de admins.** O front montava a lista por `localStorage` porque
  não havia `GET /usuarios`. Agora consome `GET /usuarios/gestores`.
- **Contatos.** Persistiam só no navegador; agora persistem no banco via
  `GET/PUT /contatos`.
- **Perfil próprio.** Modal de configurações não tinha endpoint; agora usa
  `GET/PATCH /usuarios/me`.

## 5. CRUD verificado

- **Usuários**: criar (`/usuarios/cadastro`), ler (`/me`, `/{id}`,
  `/gestores`), atualizar (`/me`, `/{id}/tipo`). "Remover admin" = rebaixar
  para CIDADAO via `/{id}/tipo` (não há exclusão física, por design).
- **Solicitações**: criar (autenticada e anônima), ler (todas, por protocolo,
  por usuário, por filtros de prioridade/bairro/categoria), atualizar status.
- **Contatos**: ler e atualizar (registro único).
- **Categorias**: leitura (derivada do enum).

## 6. Seed / dados de demonstração

`DevDataSeeder` (profile `dev`) cria no boot:
- 1 gestor (`admin@observa.com` / `admin123`) e 1 cidadão
  (`cidadao@observa.com` / `cidadao123`);
- 5 solicitações variando categoria, prioridade e status (ABERTO, TRIAGEM,
  EM_EXECUCAO, RESOLVIDO) — assim o dashboard, os gráficos e os filtros já
  aparecem populados;
- o registro padrão de contatos.

## 7. Autenticação — atalho DEV ONLY (isolado)

- **`DevDataSeeder`** e **`DevAuthController`** estão anotados com
  `@Profile("dev")`. Em produção (qualquer profile != dev) **não são
  registrados** — a rota `/auth/dev-login` simplesmente não existe.
- No `SecurityConfiguration`, o `permitAll` da rota dev é adicionado **apenas**
  quando `environment.acceptsProfiles("dev")` é verdadeiro. Ou seja, a regra
  nem entra na cadeia de segurança em produção.
- A segurança de produção **não foi enfraquecida**: o login normal continua
  validando senha com BCrypt; o filtro JWT é o mesmo; nenhuma senha fica
  embutida em código de produção.
- **Remoção para produção**: basta rodar com `spring.profiles.active=prod`
  (ou remover a linha do `application.properties`). Para eliminar de vez:
  apagar o pacote `com.obs.observaAcao.dev`, o bloco `.dev-box` em
  `login.html` e o handler dev em `js/login.js`.

## 8. Limitações conhecidas (não bloqueiam o uso)

- **Trocar o próprio e-mail** invalida o JWT atual (o `subject` do token é o
  login antigo). É preciso refazer login. Aceitável para o escopo; uma melhoria
  seria reemitir o token no `PATCH /usuarios/me`.
- **`/solicitacoes` lista todas** as solicitações para o cidadão também. Há
  `GET /solicitacoes/usuario/{id}` disponível caso queira restringir à própria
  pessoa usando o `id` agora retornado no login.
- **Recuperação de senha** é um stub seguro (responde 200 sem revelar se o
  e-mail existe). Falta a integração de envio de e-mail + token de redefinição.

## 9. Como rodar
```bash
cd observa-acao-melhorado
mvn spring-boot:run        # profile dev já ativo
# abra http://localhost:8080/login.html
```
