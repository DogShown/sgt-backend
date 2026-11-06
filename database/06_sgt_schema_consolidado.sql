/*
    SGT - SCRIPT CONSOLIDADO DO BANCO DE DADOS
    Plataforma: SQL Server 2019 Express (Somee)
    Banco esperado: sgt_db_sql

    Objetivo:
    - Criar as tabelas utilizadas atualmente pelo Backend, caso nao existam.
    - Atualizar tabelas existentes com os campos de perfil sincronizado e lembretes.
    - Padronizar os status de tarefas utilizados pela aplicacao.
    - Preservar registros existentes.

    IMPORTANTE:
    - Faça backup antes de executar em producao.
    - Execute conectado ao banco sgt_db_sql.
    - Este script nao cria usuarios de teste, nao apaga tabelas e nao remove registros.
    - Nao inclui tabela de faltas: no estado atual do projeto, a frequencia/faltas
      ainda e controlada no Frontend e nao possui persistencia implementada no Backend.
    - Categorias de tarefas continuam fixas no Frontend; nao existe tabela de categorias.
*/

SET NOCOUNT ON;
GO

IF DB_ID(N'sgt_db_sql') IS NULL
BEGIN
    THROW 50100, 'Banco sgt_db_sql nao encontrado. Selecione o banco correto no painel/cliente SQL da Somee.', 1;
END;
GO

USE sgt_db_sql;
GO

/* =========================================================
   1. USUARIOS
   ========================================================= */

IF OBJECT_ID(N'dbo.tb_usuario', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_usuario
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        nome NVARCHAR(100) NOT NULL,
        email NVARCHAR(100) NOT NULL,
        senha NVARCHAR(255) NOT NULL,
        turma NVARCHAR(50) NOT NULL,
        status NVARCHAR(255) NOT NULL
            CONSTRAINT DF_tb_usuario_status DEFAULT N'ATIVO',
        data_criacao DATETIME2 NOT NULL
            CONSTRAINT DF_tb_usuario_data_criacao DEFAULT SYSDATETIME(),

        -- Personalizacao sincronizada entre dispositivos.
        avatar_url NVARCHAR(MAX) NULL,
        cor_capa NVARCHAR(7) NULL
            CONSTRAINT DF_tb_usuario_cor_capa DEFAULT N'#075b70',
        imagem_capa NVARCHAR(MAX) NULL,

        CONSTRAINT PK_tb_usuario PRIMARY KEY (id),
        CONSTRAINT UQ_tb_usuario_email UNIQUE (email),
        CONSTRAINT CK_tb_usuario_status
            CHECK (status IN (N'ATIVO', N'INATIVO', N'BLOQUEADO'))
    );
END;
GO

-- Atualiza bancos existentes sem substituir dados de perfil.
IF COL_LENGTH(N'dbo.tb_usuario', N'avatar_url') IS NULL
    ALTER TABLE dbo.tb_usuario ADD avatar_url NVARCHAR(MAX) NULL;
GO

IF COL_LENGTH(N'dbo.tb_usuario', N'cor_capa') IS NULL
    ALTER TABLE dbo.tb_usuario ADD cor_capa NVARCHAR(7) NULL;
GO

IF COL_LENGTH(N'dbo.tb_usuario', N'imagem_capa') IS NULL
    ALTER TABLE dbo.tb_usuario ADD imagem_capa NVARCHAR(MAX) NULL;
GO

IF COL_LENGTH(N'dbo.tb_usuario', N'data_criacao') IS NULL
BEGIN
    ALTER TABLE dbo.tb_usuario
        ADD data_criacao DATETIME2 NOT NULL
            CONSTRAINT DF_tb_usuario_data_criacao_migracao DEFAULT SYSDATETIME() WITH VALUES;
END;
GO

UPDATE dbo.tb_usuario
SET cor_capa = N'#075b70'
WHERE cor_capa IS NULL OR LTRIM(RTRIM(cor_capa)) = N'';
GO

/* =========================================================
   2. TAREFAS
   ========================================================= */

IF OBJECT_ID(N'dbo.tb_tarefa', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_tarefa
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        titulo NVARCHAR(100) NOT NULL,
        descricao NVARCHAR(MAX) NULL,
        categoria NVARCHAR(50) NOT NULL,
        prioridade NVARCHAR(20) NOT NULL,
        data_entrega DATE NOT NULL,
        status NVARCHAR(255) NOT NULL
            CONSTRAINT DF_tb_tarefa_status DEFAULT N'PENDENTE',

        lembrete_prazo_enviado BIT NOT NULL
            CONSTRAINT DF_tb_tarefa_lembrete_prazo DEFAULT 0,
        lembrete_atraso_enviado BIT NOT NULL
            CONSTRAINT DF_tb_tarefa_lembrete_atraso DEFAULT 0,

        usuario_id BIGINT NOT NULL,
        data_criacao DATETIME2 NOT NULL
            CONSTRAINT DF_tb_tarefa_data_criacao DEFAULT SYSDATETIME(),

        -- Campo legado/compatibilidade, mantido caso ja exista no banco.
        data_conclusao DATETIME2 NULL,

        CONSTRAINT PK_tb_tarefa PRIMARY KEY (id),
        CONSTRAINT FK_tb_tarefa_usuario
            FOREIGN KEY (usuario_id) REFERENCES dbo.tb_usuario(id),
        CONSTRAINT CK_tb_tarefa_status CHECK
        (
            status IN
            (
                N'PENDENTE',
                N'EM_ANDAMENTO',
                N'CONCLUIDA_NO_PRAZO',
                N'CONCLUIDA_COM_ATRASO',
                N'ATRASADA'
            )
        ),
        CONSTRAINT CK_tb_tarefa_prioridade
            CHECK (prioridade IN (N'BAIXA', N'MEDIA', N'ALTA'))
    );
END;
GO

/*
   Se a tabela ja existia, os campos de lembrete sao adicionados com
   valores padrao para que os registros atuais continuem validos.
*/
IF COL_LENGTH(N'dbo.tb_tarefa', N'lembrete_prazo_enviado') IS NULL
BEGIN
    ALTER TABLE dbo.tb_tarefa
        ADD lembrete_prazo_enviado BIT NOT NULL
            CONSTRAINT DF_tb_tarefa_lembrete_prazo_migracao DEFAULT 0 WITH VALUES;
END;
GO

IF COL_LENGTH(N'dbo.tb_tarefa', N'lembrete_atraso_enviado') IS NULL
BEGIN
    ALTER TABLE dbo.tb_tarefa
        ADD lembrete_atraso_enviado BIT NOT NULL
            CONSTRAINT DF_tb_tarefa_lembrete_atraso_migracao DEFAULT 0 WITH VALUES;
END;
GO

IF COL_LENGTH(N'dbo.tb_tarefa', N'data_criacao') IS NULL
BEGIN
    ALTER TABLE dbo.tb_tarefa
        ADD data_criacao DATETIME2 NOT NULL
            CONSTRAINT DF_tb_tarefa_data_criacao_migracao DEFAULT SYSDATETIME() WITH VALUES;
END;
GO

/*
   A aplicacao utiliza exatamente estes status.
   Antes de recriar a restricao, verifica se ha valores fora do conjunto.
   O script nao converte status automaticamente para nao alterar o sentido
   de dados existentes sem uma revisao.
*/
IF EXISTS
(
    SELECT 1
    FROM dbo.tb_tarefa
    WHERE status NOT IN
    (
        N'PENDENTE',
        N'EM_ANDAMENTO',
        N'CONCLUIDA_NO_PRAZO',
        N'CONCLUIDA_COM_ATRASO',
        N'ATRASADA'
    )
       OR status IS NULL
)
BEGIN
    THROW 50101, 'Existem status de tarefa fora do padrao atual do SGT. Consulte os valores em dbo.tb_tarefa antes de aplicar a restricao.', 1;
END;
GO

DECLARE @constraintName sysname;
DECLARE @dropConstraintSql NVARCHAR(MAX);

DECLARE status_constraints CURSOR LOCAL FAST_FORWARD FOR
    SELECT name
    FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.tb_tarefa')
      AND definition LIKE N'%status%';

OPEN status_constraints;
FETCH NEXT FROM status_constraints INTO @constraintName;

WHILE @@FETCH_STATUS = 0
BEGIN
    SET @dropConstraintSql =
        N'ALTER TABLE dbo.tb_tarefa DROP CONSTRAINT ' + QUOTENAME(@constraintName) + N';';
    EXEC sys.sp_executesql @dropConstraintSql;
    FETCH NEXT FROM status_constraints INTO @constraintName;
END;

CLOSE status_constraints;
DEALLOCATE status_constraints;
GO

ALTER TABLE dbo.tb_tarefa
    ADD CONSTRAINT CK_tb_tarefa_status
    CHECK
    (
        status IN
        (
            N'PENDENTE',
            N'EM_ANDAMENTO',
            N'CONCLUIDA_NO_PRAZO',
            N'CONCLUIDA_COM_ATRASO',
            N'ATRASADA'
        )
    );
GO

/* =========================================================
   3. NOTIFICACOES PUSH
   ========================================================= */

IF OBJECT_ID(N'dbo.tb_push_subscription', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_push_subscription
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        usuario_id BIGINT NOT NULL,
        endpoint NVARCHAR(2000) NOT NULL,
        p256dh NVARCHAR(512) NOT NULL,
        auth NVARCHAR(512) NOT NULL,
        data_criacao DATETIME2 NOT NULL
            CONSTRAINT DF_tb_push_data_criacao DEFAULT SYSDATETIME(),

        CONSTRAINT PK_tb_push_subscription PRIMARY KEY (id),
        CONSTRAINT UQ_tb_push_endpoint UNIQUE (endpoint),
        CONSTRAINT FK_tb_push_usuario
            FOREIGN KEY (usuario_id) REFERENCES dbo.tb_usuario(id)
    );
END;
GO

/* =========================================================
   4. INDICES
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_tb_tarefa_usuario_id'
      AND object_id = OBJECT_ID(N'dbo.tb_tarefa')
)
    CREATE INDEX IX_tb_tarefa_usuario_id
        ON dbo.tb_tarefa(usuario_id);
GO

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_tb_tarefa_status_data'
      AND object_id = OBJECT_ID(N'dbo.tb_tarefa')
)
    CREATE INDEX IX_tb_tarefa_status_data
        ON dbo.tb_tarefa(status, data_entrega);
GO

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_tb_push_usuario_id'
      AND object_id = OBJECT_ID(N'dbo.tb_push_subscription')
)
    CREATE INDEX IX_tb_push_usuario_id
        ON dbo.tb_push_subscription(usuario_id);
GO

/* =========================================================
   5. VALIDACAO FINAL
   ========================================================= */

PRINT N'Verificacao estrutural do SGT concluida.';
GO

SELECT
    DB_NAME() AS banco_atual,
    N'dbo.tb_usuario' AS tabela,
    COUNT_BIG(*) AS total_registros
FROM dbo.tb_usuario
UNION ALL
SELECT DB_NAME(), N'dbo.tb_tarefa', COUNT_BIG(*) FROM dbo.tb_tarefa
UNION ALL
SELECT DB_NAME(), N'dbo.tb_push_subscription', COUNT_BIG(*) FROM dbo.tb_push_subscription;
GO

SELECT
    t.name AS tabela,
    c.name AS coluna,
    TYPE_NAME(c.user_type_id) AS tipo,
    c.max_length AS tamanho_bytes,
    c.is_nullable AS permite_nulo
FROM sys.tables t
INNER JOIN sys.columns c ON c.object_id = t.object_id
WHERE t.name IN (N'tb_usuario', N'tb_tarefa', N'tb_push_subscription')
ORDER BY t.name, c.column_id;
GO

SELECT status, COUNT_BIG(*) AS quantidade
FROM dbo.tb_tarefa
GROUP BY status
ORDER BY status;
GO
