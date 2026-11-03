/* Migracao OPCIONAL do modelo SQL legado para as tabelas usadas pelo Backend.
   Banco esperado: sgt_db_sql.
   Execute somente se as tabelas legadas Usuario/Tarefa existirem no MESMO banco.
   O script copia somente quando a tabela de destino esta vazia.
   Nao exclui nem altera as tabelas antigas.
   Faça backup e valide as colunas/valores do legado antes de executar. */

IF DB_ID(N'sgt_db_sql') IS NULL
    THROW 50020, 'Banco sgt_db_sql nao encontrado.', 1;
GO

USE sgt_db_sql;
GO
SET XACT_ABORT ON;
GO

BEGIN TRANSACTION;

IF OBJECT_ID(N'dbo.Usuario', N'U') IS NOT NULL
   AND OBJECT_ID(N'dbo.tb_usuario', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM dbo.tb_usuario)
BEGIN
    INSERT INTO dbo.tb_usuario (nome, email, senha, turma, status, data_criacao, rm, foto_perfil)
    SELECT
        u.nome,
        u.email,
        u.senha_hash,
        ISNULL(t.nome_turma, N'Nao definida'),
        CASE
            WHEN u.status_assinatura IN (N'Inativa', N'Cancelamento Agendado') THEN N'INATIVO'
            ELSE N'ATIVO'
        END,
        ISNULL(u.data_cadastro, SYSDATETIME()),
        u.rm,
        u.foto_perfil
    FROM dbo.Usuario u
    LEFT JOIN dbo.Turma t ON t.id_turma = u.id_turma;
END

IF OBJECT_ID(N'dbo.Tarefa', N'U') IS NOT NULL
   AND OBJECT_ID(N'dbo.tb_tarefa', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM dbo.tb_tarefa)
BEGIN
    INSERT INTO dbo.tb_tarefa (
        titulo, descricao, categoria, prioridade, data_entrega, status,
        usuario_id, data_conclusao, data_criacao
    )
    SELECT
        t.titulo,
        t.descricao,
        ISNULL(c.nome_categoria, N'Outras'),
        CASE t.prioridade
            WHEN N'Baixa' THEN N'BAIXA'
            WHEN N'Media' THEN N'MEDIA'
            WHEN N'Média' THEN N'MEDIA'
            WHEN N'Alta' THEN N'ALTA'
            ELSE N'MEDIA'
        END,
        CAST(t.data_entrega AS DATE),
        CASE t.status
            WHEN N'Pendente' THEN N'PENDENTE'
            WHEN N'Em andamento' THEN N'EM_ANDAMENTO'
            WHEN N'Concluida' THEN N'CONCLUIDA_NO_PRAZO'
            WHEN N'Concluída' THEN N'CONCLUIDA_NO_PRAZO'
            WHEN N'Concluída no prazo' THEN N'CONCLUIDA_NO_PRAZO'
            WHEN N'Concluída com atraso' THEN N'CONCLUIDA_COM_ATRASO'
            WHEN N'Atrasada' THEN N'ATRASADA'
            ELSE N'PENDENTE'
        END,
        novo_usuario.id,
        t.data_conclusao,
        ISNULL(t.data_criacao, SYSDATETIME())
    FROM dbo.Tarefa t
    INNER JOIN dbo.Usuario usuario_legado
        ON usuario_legado.id_usuario = t.id_usuario
    INNER JOIN dbo.tb_usuario novo_usuario
        ON LOWER(LTRIM(RTRIM(novo_usuario.email))) = LOWER(LTRIM(RTRIM(usuario_legado.email)))
    LEFT JOIN dbo.CategoriaTarefa c
        ON c.id_categoria = t.id_categoria;
END

COMMIT TRANSACTION;
GO

SELECT N'tb_usuario' AS tabela, COUNT(*) AS quantidade FROM dbo.tb_usuario
UNION ALL
SELECT N'tb_tarefa', COUNT(*) FROM dbo.tb_tarefa
UNION ALL
SELECT N'tb_push_subscription', COUNT(*) FROM dbo.tb_push_subscription;
GO
