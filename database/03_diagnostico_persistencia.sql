/* Diagnóstico rápido da persistência do SGT. */
USE sgt_db;
GO

SELECT DB_NAME() AS banco_atual, @@SERVERNAME AS servidor;
GO

SELECT name AS tabela
FROM sys.tables
WHERE name IN (N'Usuario', N'Tarefa', N'tb_usuario', N'tb_tarefa', N'tb_push_subscription')
ORDER BY name;
GO

SELECT * FROM dbo.tb_usuario ORDER BY id DESC;
GO

SELECT * FROM dbo.tb_tarefa ORDER BY id DESC;
GO

IF OBJECT_ID(N'dbo.Usuario', N'U') IS NOT NULL
    SELECT * FROM dbo.Usuario ORDER BY id_usuario DESC;
GO

IF OBJECT_ID(N'dbo.Tarefa', N'U') IS NOT NULL
    SELECT * FROM dbo.Tarefa ORDER BY id_tarefa DESC;
GO
