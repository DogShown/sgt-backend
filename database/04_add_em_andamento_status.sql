/* SGT - adiciona o status EM_ANDAMENTO sem apagar tarefas existentes.
   Executar uma única vez no banco sgt_db_sql antes de usar a nova movimentação. */

IF OBJECT_ID(N'dbo.tb_tarefa', N'U') IS NULL
    THROW 50001, 'A tabela dbo.tb_tarefa não existe.', 1;
GO

IF EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE name = N'CK_tb_tarefa_status'
      AND parent_object_id = OBJECT_ID(N'dbo.tb_tarefa')
)
BEGIN
    ALTER TABLE dbo.tb_tarefa DROP CONSTRAINT CK_tb_tarefa_status;
END
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
