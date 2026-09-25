# Segurança

## Estado do projeto

Este repositório é um MVP demonstrativo. Não há compromisso de suporte a versões nem garantia de adequação à produção.

As operações da API aplicam os papéis ADMIN, MANAGER e MEMBER. Credenciais inválidas retornam 401; operações não autorizadas retornam 403. A autorização utiliza o papel do usuário consultado no banco, e não o papel enviado pelo cliente.

Contas demonstrativas e a chave local ficam restritas ao perfil dev. O perfil prod exige chave JWT própria, senha de banco diferente da demonstração e CORS HTTPS explícito, desativa Swagger e impede a combinação com dev. O primeiro administrador pode ser criado por bootstrap explícito apenas em um banco sem usuários.

Siga o [guia de produção](docs/production.md) e utilize um banco separado. Trocar o perfil não remove contas demonstrativas existentes. O Compose local é destinado exclusivamente ao desenvolvimento.

O frontend ainda armazena o JWT em localStorage. Revogação de tokens, limitação de tentativas de login, recuperação de senha e auditoria completa não estão implementadas. HTTPS, backups e monitoramento precisam ser configurados no ambiente de hospedagem.

## Relatar uma vulnerabilidade

Não publique tokens, dados pessoais ou detalhes exploráveis em uma issue pública. Caso a aba **Security** do GitHub ofereça **Report a vulnerability**, use esse canal privado. Se o recurso não estiver habilitado, solicite um canal privado ao mantenedor sem revelar os detalhes da falha.

Inclua versão ou commit afetado, passos de reprodução, impacto e uma sugestão de correção quando possível.
