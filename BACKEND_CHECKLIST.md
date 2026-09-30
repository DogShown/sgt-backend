# Checklist de Avaliação — Backend SGT

Objetivo: validar o backend por etapas quando o ambiente local estiver disponível, sem alterar várias partes ao mesmo tempo.

Legenda: [OK] confirmado | [BLOQUEADO] impede funcionamento | [TESTAR] precisa de execução local | [REVISAR] ponto secundário

## 1. Inicialização
- [OK] Java 21.
- [OK] Spring Boot inicia e Tomcat é configurado na porta 8080.
- [OK] Repositórios JPA são encontrados.
- [BLOQUEADO] No último log, o JPA falhou porque o SQL Server recusou o login do usuário sa.

## 2. SQL Server
- [TESTAR] Serviço do SQL Server está em execução.
- [TESTAR] Instância aceita localhost:1433.
- [TESTAR] Banco sgt_db existe.
- [TESTAR] Login sa está habilitado.
- [TESTAR] SQL Server aceita autenticação SQL/Modo Misto.
- [TESTAR] A variável SGT_DB_PASSWORD no IntelliJ corresponde à senha atual do sa.
- [TESTAR] A mesma combinação funciona no SSMS.
- [TESTAR] Hibernate conclui a leitura dos metadados.

Não alterar hibernate.dialect para tentar mascarar o erro antes de resolver a autenticação. O erro de Dialect apareceu depois da falha de conexão.

## 3. JWT — configuração
Arquivo: src/main/java/com/sgt/sgt_api/service/JwtService.java
- [OK] Usa sgt.jwt.secret.
- [OK] Recusa segredo com menos de 32 caracteres.
- [OK] Usa HMAC256.
- [OK] Expiração configurável; padrão de 8 horas.
Arquivo: src/main/resources/application.properties
- [OK] Há fallback para desenvolvimento local.
- [REVISAR] Em produção, o segredo deve vir de variável de ambiente/secret manager, não de valor versionado.
- [OK] A aplicação possui fallback local para desenvolvimento.

## 4. JWT — login e geração
- [OK] Busca usuário por e-mail.
- [OK] Verifica status ATIVO.
- [OK] Compara senha com BCrypt.
- [OK] Subject do JWT = e-mail.
- [OK] Claim usuarioId é incluído.
- [OK] IssuedAt e ExpiresAt são incluídos.
- [TESTAR] POST /api/auth/login com credenciais válidas retorna 200 e token.
- [TESTAR] Credenciais inválidas não geram token.

## 5. JWT — filtro
Arquivo: src/main/java/com/sgt/sgt_api/config/JwtAuthenticationFilter.java
- [OK] Lê Authorization.
- [OK] Exige formato Bearer.
- [OK] Valida o token pelo JwtService.
- [OK] Usa o subject como identidade autenticada.
- [OK] Coloca autenticação no SecurityContext.
- [OK] Sessão está STATELESS.
- [REVISAR] Erros de token são ignorados silenciosamente; avaliar logging controlado depois.
- [TESTAR] /api/auth/me com token válido.
- [TESTAR] /api/auth/me sem token.
- [TESTAR] /api/auth/me com token expirado.
- [TESTAR] /api/auth/me com token inválido ou alterado.

## 6. SecurityConfig
- [OK] CSRF desabilitado.
- [OK] Sessão STATELESS.
- [OK] /api/auth/** liberado.
- [OK] Swagger liberado.
- [OK] Demais endpoints exigem autenticação.
- [OK] Filtro JWT fica antes de UsernamePasswordAuthenticationFilter.
- [TESTAR] /api/tarefas sem token deve ser recusado.
- [TESTAR] /api/tarefas com token válido deve ser acessado.

## 7. Cadastro
- [OK] POST /api/auth/cadastrar existe.
- [OK] DTO recebe nome, email, senha e turma.
- [OK] Validação de campos obrigatórios.
- [OK] Validação de formato do e-mail.
- [OK] Senha mínima de 6 caracteres.
- [OK] E-mail duplicado é verificado.
- [OK] Senha é armazenada usando BCrypt.
- [OK] Usuário nasce com status ATIVO.
- [OK] Registro é salvo pelo UsuarioRepository.
- [TESTAR] Cadastro válido -> 201.
- [TESTAR] Duplicado -> erro de regra de negócio.
- [TESTAR] Dados inválidos -> 422.
- [TESTAR] Confirmar no SQL Server que a senha não está em texto puro.

## 8. Tarefas e autorização
- [OK] Tarefa usa a identidade autenticada.
- [OK] Listagem é filtrada pelo usuário.
- [OK] Concluir e excluir verificam o dono da tarefa.
- [TESTAR] Usuário A não consegue alterar/excluir tarefa de B.
- [TESTAR] Criar, listar, concluir e excluir com token válido.
- [TESTAR] Tarefa vencida muda para ATRASADA ao listar.

## 9. CORS
Há configuração em SecurityConfig e também em WebConfig.
- [REVISAR] As listas de origens e métodos não são idênticas.
- [TESTAR] Frontend em localhost:5173 consegue login, cadastro, /me e tarefas com Authorization.

## 10. Tratamento de erros
- [OK] RuntimeException -> 400.
- [OK] Falha de validação -> 422.
- [REVISAR] Depois, diferenciar melhor 400, 401, 403, 404 e 409 para melhorar o contrato com o frontend.
- [REVISAR] Podemos adicionar um AuthenticationEntryPoint JSON específico em uma etapa posterior.

## 11. Dependências
- [REVISAR] Existe driver MySQL além do SQL Server.
- [REVISAR] WebSocket está em 4.1.0 enquanto o restante do Spring Boot está em 4.1.1.
Esses itens não são o bloqueador confirmado pelo último log.

## 12. Testes automatizados
- [OK] Existe SgtApiApplicationTests com contextLoads().
- [OK] Foi adicionado teste unitário do `JwtService` para geração/validação, segredo curto e token alterado.
- [REVISAR] Criar testes de integração para cadastro, login, `/auth/me`, endpoints protegidos e isolamento de tarefas.

## Alterações já aplicadas
- Protegido `/api/auth/me`.
- Centralizado CORS no `SecurityConfig`.
- Removido `WebConfig` de CORS duplicado.
- Definido explicitamente `org.hibernate.dialect.SQLServerDialect`.
- Criado `JwtServiceTest`.

## Ordem da próxima sessão
1. Resolver SQL Server e login sa.
2. Confirmar subida do Spring Boot sem erro 18456.
3. Rodar testes Maven.
4. Testar login e JWT.
5. Testar /auth/me.
6. Testar cadastro e validações.
7. Testar tarefas e autorização.
8. Testar React -> API.
9. Testar logout e demais telas.

## Regra de segurança para mudanças
Uma mudança por vez -> executar -> observar log -> registrar resultado -> seguir para a próxima.

Não alterar banco, JWT, SecurityConfig e frontend simultaneamente.