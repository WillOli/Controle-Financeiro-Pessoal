## Visão geral
Backend para gestão de alunos, graduações e mensalidades do Grupo de Capoeira Ginga (Mestre Agostinho e Contra Mestre Alex).

## Tech Stack
- Backend: Java 17 + Spring Boot 3
- Banco: MySQL

## Variáveis de ambiente (Windows)
Foram definidas:
- `MYSQL_USER`
- `MYSQL_PASSWORD`

Opcionalmente, você pode definir:
- `MYSQL_HOST` (padrão `localhost`)
- `MYSQL_PORT` (padrão `3306`)
- `MYSQL_DATABASE` (padrão `controle_financeiro`)

## Como executar
1. Garanta que o MySQL está rodando e que o banco exista:
   - `CREATE DATABASE controle_financeiro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
2. No diretório do projeto, execute:
   - `mvn spring-boot:run`
3. Verifique o status:
   - `http://localhost:8080/health`

## Configuração
- `spring.datasource.url` usa as variáveis de ambiente para host, porta e nome do banco.
- `spring.jpa.hibernate.ddl-auto=update` para criar/atualizar tabelas conforme entidades (desenvolvimento).

## Próximos passos
- Modelar entidades: `Aluno`, `Graduacao`, `HistoricoGraduacao`, `Mensalidade`, `Usuario`.
- Criar repositórios, serviços e controllers.
- Adicionar autenticação com Spring Security (BCrypt + roles).
