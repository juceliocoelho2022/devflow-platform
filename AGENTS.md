# DevFlow Enterprise

## Visão geral

Monorepo de uma plataforma de gestão de projetos, tarefas, equipes e Kanban.

## Estrutura

- `backend/`: API REST em Java 21, Spring Boot 3 e Maven.
- `frontend/`: SPA React, TypeScript e Vite.
- `docs/project-spec.md`: escopo e decisões do produto.

## Comandos

- Backend: `cd backend && mvn spring-boot:run`
- Testes backend: `cd backend && mvn test`
- Frontend: `cd frontend && npm install && npm run dev`
- Build frontend: `cd frontend && npm run build`
- Stack completa: `docker compose up --build`

## Convenções

- Código e nomes técnicos em inglês; interface e documentação do produto em português.
- APIs sob `/api`; DTOs não expõem entidades JPA diretamente.
- Nunca versionar segredos. Valores locais devem usar `.env`, baseado em `.env.example`.
- Toda mudança deve manter o build e os testes relevantes passando.

## Guardrails

- Preserve o tema visual azul-marinho/azul elétrico.
- O MVP é single-tenant. Não introduza isolamento multi-tenant sem atualizar a especificação.
- Autorização é baseada em papéis `ADMIN`, `MANAGER` e `MEMBER`.
