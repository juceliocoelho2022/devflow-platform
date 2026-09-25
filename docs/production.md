# Preparação para produção

Esta configuração prepara o MVP para um ambiente separado de desenvolvimento. O deploy, o domínio, o certificado HTTPS, backups e monitoramento ainda precisam ser provisionados. Não use o Compose demonstrativo para publicar o serviço.

## Papéis e operações

| Operação | ADMIN | MANAGER | MEMBER |
| --- | --- | --- | --- |
| Consultar dashboard, projetos e tarefas | Sim | Sim | Sim |
| Criar projetos e tarefas | Sim | Sim | Não |
| Movimentar tarefas | Sim | Sim | Sim |

O workspace continua single-tenant: os três papéis consultam os mesmos dados e podem movimentar qualquer tarefa. Não existe isolamento por equipe, projeto ou responsável. ADMIN e MANAGER têm as mesmas permissões nas operações atuais; a gestão de usuários ainda não está implementada.

As regras são aplicadas na API. Alterar o papel no navegador não concede acesso. O backend consulta o papel atual do usuário no banco ao validar o JWT. Requisições sem autenticação válida retornam `401`; falta de permissão retorna `403`, ambos em JSON.

## Perfis

- `dev`: H2 em memória, contas demonstrativas e chave JWT local. É o perfil padrão para desenvolvimento.
- `dev,docker`: demonstração local com PostgreSQL, via `docker-compose.yml`.
- `prod`: PostgreSQL, Flyway, chave JWT obrigatória, CORS HTTPS explícito, sem contas demonstrativas e sem Swagger público.

Não combine `dev` e `prod`; a aplicação recusa essa combinação. Use a configuração de produção abaixo em um banco novo. Trocar o perfil não remove contas demonstrativas que já existam em um banco antigo.

## Configurar o ambiente

1. Copie `.env.production.example` para `.env.production`.
2. Preencha `POSTGRES_PASSWORD` com uma senha exclusiva.
3. Preencha `JWT_SECRET` com um segredo aleatório de pelo menos 32 bytes. Não reutilize a chave de desenvolvimento nem os placeholders dos exemplos.
4. Defina `CORS_ORIGIN` com a origem HTTPS real, sem barra final, caminho ou curinga. Separe múltiplas origens por vírgula.
5. No primeiro início, habilite `APP_BOOTSTRAP_ENABLED=true` e preencha nome, e-mail e senha do administrador. A senha deve ter pelo menos 16 caracteres e no máximo 72 bytes em UTF-8.

Gere segredos em um gerenciador de senhas ou ferramenta criptográfica. Não cole valores reais em issues, pull requests ou logs.

Valide e inicie:

```bash
docker compose --env-file .env.production -f docker-compose.production.yml config --quiet
docker compose --env-file .env.production -f docker-compose.production.yml up --build -d
```

O Compose utiliza um volume próprio chamado `production_data`, mantém PostgreSQL e API sem portas publicadas e expõe o frontend apenas em `127.0.0.1:3000`.

Configure um proxy HTTPS no host para encaminhar a origem pública para `http://127.0.0.1:3000`. O Nginx do frontend encaminha `/api` internamente para a API. Se seu provedor usa um proxy em container ou rede externa, adapte o encaminhamento a essa rede; a porta loopback só atende no próprio host.

## Primeiro administrador

O bootstrap está desativado por padrão. Quando habilitado, cria exatamente um ADMIN somente se o banco não tiver usuários. A senha é persistida como hash BCrypt. Ele não redefine senhas, não promove usuários existentes e não serve como mecanismo de recuperação de conta.

Após confirmar o primeiro login:

1. Altere `APP_BOOTSTRAP_ENABLED=false`.
2. Remova a senha e os demais valores de bootstrap de `.env.production`.
3. Recrie o backend com o mesmo comando `up --build -d` para remover essas variáveis do container.

Execute apenas uma instância do backend durante a inicialização do primeiro administrador. O bootstrap não coordena múltiplas réplicas concorrentes.

## Validação antes de liberar acesso

- Login com o administrador configurado; credenciais demonstrativas devem falhar.
- Criação de projeto e tarefa e movimentação no Kanban.
- Token inválido ou expirado retorna `401`; MEMBER criando projeto ou tarefa retorna `403`.
- Swagger desativado e API/banco sem exposição direta.
- HTTPS, persistência e restauração de backup verificados no ambiente real.

A suíte de integração cobre login, operações por papel e tokens inválidos/expirados. Testes adicionais cobrem o bootstrap e a rejeição de configurações de produção inseguras. Isso não substitui a validação do ambiente hospedado.

## Limites restantes

JWT ainda é armazenado em localStorage e não possui revogação ou refresh token. Limitação de tentativas de login, gestão de usuários, redefinição de senha, auditoria, backups e monitoramento operacional ainda não foram implementados. As séries de produtividade e atividades do dashboard continuam demonstrativas. Avalie esses itens antes de disponibilizar o serviço para uso real.
