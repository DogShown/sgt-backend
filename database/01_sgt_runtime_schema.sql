/* SGT - schema oficial das tabelas usadas pelo Backend atual - SQL Server.
O script NAO apaga dados existentes. */

IF DB_ID(N'sgt_db') IS NULL
BEGIN
    CREATE DATABASE sgt_db;
END
GO

USE sgt_db;
GO

IF OBJECT_ID(N'dbo.tb_usuario', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_usuario (
        id BIGINT IDENTITY(1,1) NOT NULL,
        nome NVARCHAR(100) NOT NULL,
        email NVARCHAR(100) NOT NULL,
        senha NVARCHAR(255) NOT NULL,
        turma NVARCHAR(50) NOT NULL,
        status NVARCHAR(20) NOT NULL CONSTRAINT DF_tb_usuario_status DEFAULT N'ATIVO',
        data_criacao DATETIME2 NOT NULL CONSTRAINT DF_tb_usuario_data_criacao DEFAULT SYSDATETIME(),
        rm VARCHAR(20) NULL,
        foto_perfil NVARCHAR(255) NULL,
        CONSTRAINT PK_tb_usuario PRIMARY KEY (id),
        CONSTRAINT UQ_tb_usuario_email UNIQUE (email),
        CONSTRAINT CK_tb_usuario_status CHECK (status IN (N'ATIVO', N'INATIVO', N'BLOQUEADO'))
    );
END
GO

IF OBJECT_ID(N'dbo.tb_tarefa', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_tarefa (
        id BIGINT IDENTITY(1,1) NOT NULL,
        titulo NVARCHAR(100) NOT NULL,
        descricao NVARCHAR(MAX) NULL,
        categoria NVARCHAR(50) NOT NULL,
        prioridade NVARCHAR(20) NOT NULL,
        data_entrega DATE NOT NULL,
        status NVARCHAR(30) NOT NULL CONSTRAINT DF_tb_tarefa_status DEFAULT N'PENDENTE',
        lembrete_prazo_enviado BIT NOT NULL CONSTRAINT DF_tb_tarefa_lembrete_prazo DEFAULT 0,
        lembrete_atraso_enviado BIT NOT NULL CONSTRAINT DF_tb_tarefa_lembrete_atraso DEFAULT 0,
        data_conclusao DATETIME2 NULL,
        data_criacao DATETIME2 NOT NULL CONSTRAINT DF_tb_tarefa_data_criacao DEFAULT SYSDATETIME(),
        usuario_id BIGINT NOT NULL,
        CONSTRAINT PK_tb_tarefa PRIMARY KEY (id),
        CONSTRAINT FK_tb_tarefa_usuario FOREIGN KEY (usuario_id) REFERENCES dbo.tb_usuario(id),
        CONSTRAINT CK_tb_tarefa_status CHECK (
            status IN (N'PENDENTE', N'CONCLUIDA_NO_PRAZO', N'CONCLUIDA_COM_ATRASO', N'ATRASADA')
        ),
        CONSTRAINT CK_tb_tarefa_prioridade CHECK (prioridade IN (N'BAIXA', N'MEDIA', N'ALTA'))
    );
END
GO

IF OBJECT_ID(N'dbo.tb_push_subscription', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_push_subscription (
        id BIGINT IDENTITY(1,1) NOT NULL,
        usuario_id BIGINT NOT NULL,
        endpoint NVARCHAR(2000) NOT NULL,
        p256dh NVARCHAR(512) NOT NULL,
        auth NVARCHAR(512) NOT NULL,
        data_criacao DATETIME2 NOT NULL CONSTRAINT DF_tb_push_data_criacao DEFAULT SYSDATETIME(),
        CONSTRAINT PK_tb_push_subscription PRIMARY KEY (id),
        CONSTRAINT UQ_tb_push_endpoint UNIQUE (endpoint),
        CONSTRAINT FK_tb_push_usuario FOREIGN KEY (usuario_id) REFERENCES dbo.tb_usuario(id)
    );
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_tb_tarefa_usuario_id' AND object_id = OBJECT_ID(N'dbo.tb_tarefa')
)
BEGIN
    CREATE INDEX IX_tb_tarefa_usuario_id ON dbo.tb_tarefa(usuario_id);
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_tb_tarefa_status_data' AND object_id = OBJECT_ID(N'dbo.tb_tarefa')
)
BEGIN
    CREATE INDEX IX_tb_tarefa_status_data ON dbo.tb_tarefa(status, data_entrega);
END
GO
