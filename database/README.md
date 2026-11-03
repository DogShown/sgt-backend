# Banco de dados do SGT

## Banco utilizado pelo Backend

O Backend está configurado para conectar ao SQL Server Somee no banco **sgt_db_sql**. Configure no ambiente de execução:

- `SGT_DB_USERNAME`
- `SGT_DB_PASSWORD`

Não coloque credenciais no repositório.

Tabelas usadas diretamente pelo Backend:

- **dbo.tb_usuario** — contas dos usuários;
- **dbo.tb_tarefa** — tarefas;
- **dbo.tb_push_subscription** — inscrições de Web Push.

## Ordem segura de aplicação

1. Confirme no painel/cliente SQL que a conexão está selecionando **sgt_db_sql**.
2. Faça backup do banco antes de qualquer alteração.
3. Execute `01_sgt_runtime_schema.sql`. Ele cria somente tabelas/índices ausentes; não cria outro banco nem apaga registros.
4. Execute `03_diagnostico_persistencia.sql` e confira as tabelas e quantidades.
5. Se a tabela `tb_tarefa` já existia com uma restrição que não aceita `EM_ANDAMENTO`, execute `04_add_em_andamento_status.sql` uma única vez. A migração substitui as restrições CHECK de status e preserva os registros.
6. Execute `02_migrate_legacy_sgt.sql` **somente** se o banco atual também contiver as tabelas legadas `Usuario`, `Tarefa`, `Turma` e `CategoriaTarefa`, e se a migração tiver sido revisada. Ela copia dados apenas quando a tabela de destino está vazia e não exclui as tabelas antigas.

## Atenção à migração legada

O script 02 pressupõe nomes e colunas específicos do modelo antigo. Antes de executá-lo, confirme que essas tabelas existem no mesmo banco e que os valores textuais de status/prioridade correspondem aos mapeamentos do script. Não execute em produção apenas para testar.

## Diagnóstico

Use `03_diagnostico_persistencia.sql` para conferir a persistência. O Backend não deve apontar para um banco diferente daquele em que os scripts foram executados.

O schema do runtime é intencionalmente enxuto. Categorias continuam fixas no Frontend; não há tabela de categorias gerenciáveis neste momento.
