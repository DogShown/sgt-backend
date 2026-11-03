/* SGT - permite o status EM_ANDAMENTO sem apagar tarefas.
   Banco esperado: sgt_db_sql.
   Execute somente depois de confirmar o banco e fazer backup.
   Pode ser executado novamente: recria a restrição com o conjunto oficial. */

IF DB_ID(N'sgt_db_sql') IS NULL
    THROW 50010, 'Banco sgt_db_sql nao encontrado.', 1;
GO

USE sgt_db_sql;
GO

IF OBJECT_ID(N'dbo.tb_tarefa', N'U') IS NULL
    THROW 50011, 'A tabela dbo.tb_tarefa nao existe neste banco.', 1;
GO

DECLARE @constraintName sysname;
DECLARE @dropSql nvarchar(max);

DECLARE status_constraints CURSOR LOCAL FAST_FORWARD FOR
    SELECT name
    FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.tb_tarefa')
      AND definition LIKE N'%status%';

OPEN status_constraints;
FETCH NEXT FROM status_constraints INTO @constraintName;

WHILE @@FETCH_STATUS = 0
BEGIN
    SET @dropSql = N'ALTER TABLE dbo.tb_tarefa DROP CONSTRAINT ' + QUOTENAME(@constraintName) + N';';
    EXEC sys.sp_executesql @dropSql;
    FETCH NEXT FROM status_constraints INTO @constraintName;
END;

CLOSE status_constraints;
DEALLOCATE status_constraints;
GO

ALTER TABLE dbo.tb_tarefa
ADD CONSTRAINT CK_tb_tarefa_status CHECK (
    status IN (
        N'PENDENTE',
        N'EM_ANDAMENTO',
        N'CONCLUIDA_NO_PRAZO',
        N'CONCLUIDA_COM_ATRASO',
        N'ATRASADA'
    )
);
GO

PRINT N'Migracao concluida: EM_ANDAMENTO permitido.';
GO
