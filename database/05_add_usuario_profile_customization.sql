/* SGT - adiciona personalização sincronizada à conta do usuário.
   Seguro para executar mais de uma vez; não remove nem altera dados existentes. */

IF COL_LENGTH('dbo.tb_usuario', 'avatar_url') IS NULL
    ALTER TABLE dbo.tb_usuario ADD avatar_url NVARCHAR(MAX) NULL;
GO

IF COL_LENGTH('dbo.tb_usuario', 'cor_capa') IS NULL
    ALTER TABLE dbo.tb_usuario ADD cor_capa NVARCHAR(7) NULL;
GO

IF COL_LENGTH('dbo.tb_usuario', 'imagem_capa') IS NULL
    ALTER TABLE dbo.tb_usuario ADD imagem_capa NVARCHAR(MAX) NULL;
GO

UPDATE dbo.tb_usuario
SET cor_capa = N'#075b70'
WHERE cor_capa IS NULL OR LTRIM(RTRIM(cor_capa)) = N'';
GO

PRINT N'Campos de perfil sincronizado adicionados/verificados.';
GO