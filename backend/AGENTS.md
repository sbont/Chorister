# Chorister Backend

Spring Boot 3.5 / Kotlin backend (Java 21, Gradle, Liquibase, JPA).

## Conventions

- Keep comments to a minimum. Only comment non-obvious behavior.
- Commit messages: short, imperative, lowercase-ish (match `git log`).

## Build and run

- Build: `./gradlew build`
- Tests: `./gradlew test`
- Run locally: start the stack from the repo root with `docker compose up -d` (CockroachDB, Postgres/Zitadel, MinIO, Caddy), then `./gradlew bootRun`.

## Migrations

- Schema changes go through Liquibase. Add a changeset to `src/main/resources/db/changelog/`, then generate the diff by running:

  ```
  ./gradlew diffChangelog
  ```

- After generating, apply with `./gradlew update`. Always ask for confirmation before running it.

## Commits

- Never commit unless the user explicitly asks or approves it.
