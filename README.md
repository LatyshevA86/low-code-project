# Low-code-project

Микросервисный прототип low-code платформы (модуль `workflow-service`).

## workflow-service

Исполнение workflow по JSON-схеме (`starter` + `activities[]`):

- **Temporal** — durable execution, отдельные activity (`AI_AGENT`, `REST_CALL`)
- **Контекст** — переменные накапливаются в workflow (`Map<String, JsonNode>`), плейсхолдеры вида `$starter.recordId`,
  `$activity-1.score`
- **Typed config** — для каждого типа activity свой record (`AiAgentConfig`, `RestCallConfig`, `ConditionConfig`)
- **Postgres + Flyway** — хранение `workflows` и `workflow_definitions`

### Локальный запуск

1. Поднять инфраструктуру (Temporal + Postgres):

```bash
cd docker
docker compose up -d
```

2. Запустить сервис:

```bash
./gradlew :workflow-service:bootRun
```

Postgres: `localhost:5433`, БД `workflow-db`, пользователь/пароль `postgres`/`postgres` (Temporal использует `temporal`/`temporal`).

### Тесты

```bash
./gradlew :workflow-service:test
```
