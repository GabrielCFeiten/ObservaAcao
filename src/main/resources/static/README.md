# Observa Ação — Front-end (servido pelo backend)

Estes arquivos estáticos são servidos pelo próprio Spring Boot em
`src/main/resources/static/`, então todas as chamadas usam caminhos
relativos (sem CORS).

## Rodar
```bash
mvn spring-boot:run
```
Acesse `http://localhost:8080/login.html`.

O profile `dev` já vem ativo (`application.properties`), então o banco é
populado automaticamente e há atalhos de acesso rápido na tela de login.

## Acesso de demonstração (profile dev)
- Gestor:  `admin@observa.com` / `admin123`
- Cidadão: `cidadao@observa.com` / `cidadao123`
- Ou use os botões "Acesso rápido (dev)" na tela de login.

## Páginas e endpoints
| Página | Acesso | Endpoints |
|---|---|---|
| login | público | POST /auth/login · POST /auth/dev-login (dev) |
| register | público | POST /usuarios/cadastro |
| forgot-password | público | POST /auth/recuperar-senha |
| home | autenticado | GET /categorias · GET /solicitacoes · POST /solicitacoes |
| admin-dashboard | GESTOR | GET /solicitacoes · PATCH /solicitacoes/{protocolo}/status |
| admin-contacts | GESTOR | GET /contatos · PUT /contatos |
| admin-management | GESTOR | GET /usuarios/gestores · POST /usuarios/cadastro · PATCH /usuarios/{id}/tipo |
| (perfil) | autenticado | GET /usuarios/me · PATCH /usuarios/me |

## Produção
Troque o profile para algo diferente de `dev` (ex.: `-Dspring.profiles.active=prod`).
Isso desliga o seeder e o `/auth/dev-login`, e remove os atalhos. Para
eliminar de vez, apague o pacote `com.obs.observaAcao.dev` e o bloco
`.dev-box` em `login.html` + o handler em `js/login.js`.
