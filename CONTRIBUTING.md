# Como contribuir

Comece por uma issue que descreva o problema ou a melhoria. Para alterações pequenas e correções, abra um pull request com contexto suficiente para revisão.

## Ambiente

Siga o [README](README.md) para iniciar o backend e o frontend. Use Java 21, Maven 3.9+ e Node.js 22.12+. Instale as dependências com `npm ci` para respeitar o lockfile.

## Convenções

- Código e nomes técnicos em inglês; interface e documentação do produto em português.
- Preserve o tema azul-marinho/azul elétrico e o escopo single-tenant.
- Rotas HTTP sob `/api`; exponha DTOs, não entidades JPA.
- Nunca inclua tokens, senhas pessoais ou arquivos `.env` nos commits.
- Mantenha alterações focadas e descreva mudanças de comportamento.
- Atualize a especificação quando alterar decisões de produto ou contratos.

## Antes do pull request

1. Execute `npm test` e `npm run build` em `frontend`.
2. Execute `mvn -B verify` em `backend`.
3. Revise os arquivos incluídos: não envie ZIPs, dependências ou artefatos de build.
4. Explique o problema, a solução e os testes executados.
5. Para mudanças visuais, inclua uma captura real da interface sem dados sensíveis.

Se algum teste não puder ser executado, registre a limitação no pull request. Não declare um teste como aprovado sem executá-lo.
