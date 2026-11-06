-- Novas tabelas do SGT: executar uma única vez no banco sgt_db_sql.
-- O backend também pode criá-las via spring.jpa.hibernate.ddl-auto=update.

IF OBJECT_ID('dbo.tb_registro_frequencia', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_registro_frequencia (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        data DATE NOT NULL,
        quantidade INT NOT NULL,
        materia NVARCHAR(100) NULL,
        justificada BIT NOT NULL CONSTRAINT DF_frequencia_justificada DEFAULT 0,
        unidade NVARCHAR(10) NOT NULL CONSTRAINT DF_frequencia_unidade DEFAULT 'dias',
        CONSTRAINT FK_frequencia_usuario FOREIGN KEY (usuario_id) REFERENCES dbo.tb_usuario(id)
    );
    CREATE INDEX IX_frequencia_usuario_data ON dbo.tb_registro_frequencia(usuario_id, data DESC);
END;

IF OBJECT_ID('dbo.tb_token_conta', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.tb_token_conta (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        token_hash NVARCHAR(64) NOT NULL,
        finalidade NVARCHAR(30) NOT NULL,
        expira_em DATETIME2 NOT NULL,
        consumido_em DATETIME2 NULL,
        email_destino NVARCHAR(100) NULL,
        CONSTRAINT UQ_token_conta_hash UNIQUE(token_hash),
        CONSTRAINT FK_token_conta_usuario FOREIGN KEY (usuario_id) REFERENCES dbo.tb_usuario(id)
    );
    CREATE INDEX IX_token_conta_usuario_finalidade ON dbo.tb_token_conta(usuario_id, finalidade);
END;
