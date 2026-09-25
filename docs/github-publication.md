# Publicação no GitHub

Destino: https://github.com/juceliocoelho2022/devflow-platform

## Apresentação do repositório

**Descrição sugerida:** Plataforma full-stack de gestão de projetos e Kanban com Java 21, Spring Boot, React e TypeScript. MVP com JWT, PostgreSQL e Docker Compose.

**Topics sugeridos:** `java`, `spring-boot`, `react`, `typescript`, `postgresql`, `kanban`, `project-management`, `docker`, `fullstack`, `portfolio`.

Deixe o campo Website vazio até existir uma demonstração acessível. Adicione capturas reais do dashboard e Kanban ao README quando disponíveis. Não use mockups como evidência de funcionalidades implementadas.

## Publicação

1. Autentique o GitHub CLI com `gh auth login --hostname github.com`.
2. Inspecione o conteúdo e a branch padrão do repositório remoto antes de enviar os arquivos.
3. Se o remoto já tiver commits, integre as alterações preservando seu histórico. Não use push forçado para substituir o projeto.
4. Revise o conjunto de arquivos com `git status` e `git diff --cached` antes do commit.
5. Publique as alterações e acompanhe a execução do workflow CI.

## Ajustes no GitHub

- Preencha a descrição e os topics acima.
- Habilite o relato privado de vulnerabilidades, se disponível.
- Após a primeira execução bem-sucedida, configure proteção da branch padrão com os dois jobs de CI obrigatórios.
- Escolha uma licença de acordo com a política de reutilização desejada antes de adicionar um arquivo LICENSE.
- Use Releases para distribuir versões; os ZIPs locais não fazem parte do código-fonte.

O diretório `portfolio/` é um site pessoal independente e não é publicado automaticamente pelo workflow de CI.
