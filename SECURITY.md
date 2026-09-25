# Segurança

## Estado do projeto

Este repositório é um MVP demonstrativo. Não há compromisso de suporte a versões nem garantia de adequação à produção.

O código atual contém contas públicas de demonstração, uma chave JWT de fallback para desenvolvimento e um inicializador de dados que também roda no perfil Docker. Os papéis estão modelados, mas as operações ainda precisam de restrições e testes específicos de autorização. O frontend armazena o JWT em localStorage.

Antes de uma implantação pública, substitua a configuração demonstrativa, restrinja o inicializador, implemente a autorização por papel e revise o armazenamento de sessão, HTTPS, CORS e gestão de segredos.

## Relatar uma vulnerabilidade

Não publique tokens, dados pessoais ou detalhes exploráveis em uma issue pública. Caso a aba **Security** do GitHub ofereça **Report a vulnerability**, use esse canal privado. Se o recurso não estiver habilitado, solicite um canal privado ao mantenedor sem revelar os detalhes da falha.

Inclua versão ou commit afetado, passos de reprodução, impacto e uma sugestão de correção quando possível.
