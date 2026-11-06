# Configuração de ambiente do SGT

Este documento descreve as variáveis que precisam existir no ambiente do backend. Não coloque senhas, tokens ou chaves privadas no Git.

## Banco SQL Server (Somee)

- `SGT_DB_USERNAME`: usuário do banco.
- `SGT_DB_PASSWORD`: senha do banco.
- `SGT_JWT_SECRET`: segredo aleatório com pelo menos 32 caracteres. Gere um valor exclusivo para produção; não reutilize o valor de desenvolvimento.

## E-mail transacional (SMTP)

Os fluxos de recuperação de senha e confirmação de alteração de e-mail só enviam mensagens quando `SGT_MAIL_ENABLED=true`.

- `SGT_MAIL_ENABLED=true`
- `SGT_MAIL_HOST`: servidor SMTP do provedor escolhido.
- `SGT_MAIL_PORT`: normalmente 587 para STARTTLS ou 465 para TLS implícito, conforme o provedor.
- `SGT_MAIL_USERNAME`: usuário SMTP.
- `SGT_MAIL_PASSWORD`: senha SMTP ou senha de aplicativo.
- `SGT_MAIL_FROM`: endereço remetente autorizado pelo provedor.
- `SGT_MAIL_SMTP_AUTH=true`
- `SGT_MAIL_STARTTLS=true` para provedores que usam STARTTLS.
- `SGT_FRONTEND_URL`: URL pública do frontend, sem barra final (ex.: domínio Vercel do SGT).

Use as configurações oficiais do provedor de e-mail. Não compartilhe senha SMTP no chat nem em commits. Em hospedagens como Render, cadastre esses valores na área de Environment Variables/Secrets.

## CORS e notificações

- `SGT_CORS_ALLOWED_ORIGINS`: lista separada por vírgulas com as origens exatas do frontend.
- `SGT_VAPID_PUBLIC_KEY`, `SGT_VAPID_PRIVATE_KEY` e `SGT_VAPID_SUBJECT`: chaves de Web Push, se as notificações push forem usadas.

## Banco: novas tabelas de frequência e tokens

A aplicação usa Hibernate com `ddl-auto=update`, mas o script versionado também pode ser aplicado manualmente no banco correto:

`database/07_sgt_conta_frequencia.sql`

Execute-o uma vez no banco `sgt_db_sql`, com uma conta autorizada. O script é idempotente para as tabelas nele declaradas.

## Verificação segura

1. Configure os valores no ambiente de hospedagem, nunca no repositório.
2. Reinicie o serviço.
3. Confira os logs de inicialização sem imprimir variáveis secretas.
4. Solicite recuperação de senha usando uma conta de teste e confirme a entrega da mensagem.
5. Teste o link dentro do prazo de 30 minutos.
6. Teste alteração de e-mail com uma caixa de entrada controlada.
7. Não use dados pessoais reais de estudantes nos testes.

A compilação do projeto não valida acesso real ao SQL Server nem a entrega SMTP. Essas verificações precisam ser feitas no ambiente configurado.

## Smoke test real do SQL Server

Foi adicionado o workflow manual `.github/workflows/sqlserver-integration.yml`. Para executá-lo sem expor credenciais:

1. Em GitHub → Settings → Secrets and variables → Actions, crie os secrets `SGT_DB_USERNAME` e `SGT_DB_PASSWORD`.
2. Abra a aba Actions do repositório.
3. Selecione **SQL Server integration smoke test** e execute **Run workflow** na branch `feature/sgt-core-completion`.
4. O teste executa somente `SELECT 1`; não cria, altera ou remove dados do banco.

Esse teste confirma conectividade e autenticação. Ele não substitui um teste de integração CRUD completo com contas de teste.
