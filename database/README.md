# Banco de dados do SGT

## Banco usado pelo Backend

O Backend atual aponta para o SQL Server local no banco **sgt_db**.

As tabelas de persistência usadas diretamente pelo JPA/Hibernate são:

- **tb_usuario** — contas dos usuários;
- **tb_tarefa** — tarefas;
- **tb_push_subscription** — inscrições de Web Push.

O código Java usa exatamente esses nomes. Portanto, os registros criados pelo Frontend devem ser conferidos nessas tabelas.

## Ordem recomendada

1. Execute 01_sgt_runtime_schema.sql.
2. Se o banco antigo já possuir as tabelas Usuario e Tarefa do modelo inicial, execute 02_migrate_legacy_sgt.sql.
3. Execute 03_diagnostico_persistencia.sql para confirmar onde os registros estão.

Nenhum dos scripts de migração remove as tabelas antigas.

## Sobre o modelo original

O primeiro modelo do TCC foi mais amplo e já previa módulos como Turma, GrupoTCC, CategoriaTarefa, MensagemChat, Notificacao e HistóricoDesempenho.

O Backend atual foi implementado primeiro com um núcleo menor. Este diretório documenta a compatibilidade entre esse núcleo e o modelo SQL anterior.

Depois que o núcleo estiver estável, os módulos maiores podem ser integrados ao JPA sem refazer o sistema inteiro.
