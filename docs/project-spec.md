# DevFlow Enterprise — Especificação da versão 0.2

## Objetivo

Entregar uma primeira versão funcional de uma plataforma para equipes de tecnologia organizarem projetos, tarefas e fluxo Kanban em uma interface inspirada na referência visual fornecida.

## Decisões

| ID | Estado | Decisão |
|---|---|---|
| D01 | Confirmado pela referência | Java 21 e Spring Boot 3 no backend |
| D02 | Confirmado pela referência | React no frontend |
| D03 | Confirmado pela referência | PostgreSQL em produção/Docker |
| D04 | Confirmado pela referência | API REST, JWT e RBAC |
| D05 | Confirmado pela referência | Docker, Swagger e Flyway |
| D06 | Assumido para o MVP | Aplicação single-tenant com dados demonstrativos |
| D07 | Assumido para o MVP | Vite + TypeScript no frontend |
| D08 | Implementado na 0.2 | Controllers, services, repositories, DTOs e mappers separados |
| D09 | Implementado na 0.2 | Kanban com drag-and-drop, busca e filtros locais |
| D10 | Implementado na 0.2 | Erros de API padronizados e filtros combináveis |

## Escopo

- Autenticação com usuário demonstrativo.
- Visão geral com indicadores, gráfico de produtividade e atividades.
- Listagem e cadastro de projetos.
- Listagem, criação e movimentação de tarefas no Kanban.
- Busca e filtros por projeto e prioridade no Kanban.
- Movimentação de cartões por drag-and-drop.
- Navegação para equipes, usuários, relatórios e configurações.
- Layout responsivo.
- API documentada por OpenAPI.
- Execução local e com Docker Compose.

## Fora do MVP

- Notificações em tempo real, anexos reais, e-mail e integrações externas.
- Multi-tenancy, cobrança, auditoria completa e recuperação de senha por e-mail.
- Relatórios exportáveis e gestão avançada de permissões.

## Modelo

- User: identidade, e-mail, papel e avatar.
- Project: nome, descrição, status e datas.
- Task: título, descrição, status, prioridade, responsável, projeto e prazo.
- Activity: registro resumido de eventos exibidos no dashboard.

## Segurança

JWT stateless, senhas com BCrypt, CORS configurável e papéis `ADMIN`, `MANAGER` e `MEMBER`. O usuário de demonstração é criado apenas para desenvolvimento e deve ser substituído antes de produção.

## Critérios de aceite

- Login retorna token válido.
- Dashboard consolida dados da API.
- Projetos e tarefas podem ser criados.
- Tarefas podem mudar de coluna no Kanban.
- Tarefas podem ser encontradas por busca e filtros.
- Erros de validação retornam código, mensagem, timestamp e campos inválidos.
- Frontend compila sem erros TypeScript.
- Backend passa nos testes e inicia com perfil local.
- `docker compose up --build` descreve a stack completa.
