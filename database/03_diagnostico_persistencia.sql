/* Diagnostico seguro da persistencia do SGT.
   Nao retorna nomes, emails, senhas/hash ou conteudo pessoal. */

IF DB_ID(N'sgt_db_sql') IS NULL
    THROW 50030, 'Banco sgt_db_sql nao encontrado.', 1;
GO

USE sgt_db_sql;
GO

SELECT DB_NAME() AS banco_atual, @@SERVERNAME AS servidor;
GO

SELECT
    tabela,
    CASE WHEN OBJECT_ID(N'dbo.' + tabela, N'U') IS NULL THEN N'AUSENTE' ELSE N'EXISTE' END AS situacao
FROM (VALUES
    (N'tb_usuario'),
    (N'tb_tarefa'),
    (N'tb_push_subscription'),
    (N'Usuario'),
    (N'Tarefa'),
    (N'Turma'),
    (N'CategoriaTarefa')
) AS tabelas(tabela);
GO

IF OBJECT_ID(N'dbo.tb_usuario', N'U') IS NOT NULL
    SELECT COUNT_BIG(*) AS total_usuarios FROM dbo.tb_usuario;
GO

IF OBJECT_ID(N'dbo.tb_tarefa', N'U') IS NOT NULL
BEGIN
    SELECT COUNT_BIG(*) AS total_tarefas FROM dbo.tb_tarefa;

    SELECT status, COUNT_BIG(*) AS quantidade
    FROM dbo.tb_tarefa
    GROUP BY status
    ORDER BY status;
END
GO

IF OBJECT_ID(N'dbo.tb_push_subscription', N'U') IS NOT NULL
    SELECT COUNT_BIG(*) AS total_inscricoes_push FROM dbo.tb_push_subscription;
GO

SELECT name AS restricao_check, definition
FROM sys.check_constraints
WHERE parent_object_id = OBJECT_ID(N'dbo.tb_tarefa');
GO
