# Low-code-project

Микросервисный прототип low-code платформы (модуль `workflow-service`).

Исполнение workflow по JSON-схеме (`starter` + `activities[]`):

## Запуск

### 1. Контейнеры (Temporal + Postgres)

Инфраструктура описана в `docker/docker-compose.yml`

| Контейнер | Образ | Порты (host) | Назначение |
|---|---|---|---|
| `temporal` | `temporalio/auto-setup:1.25.2` | `7233`, `8233` | Temporal server (gRPC frontend — `7233`) |
| `temporal-ui` | `temporalio/ui:2.53.3` | `8080` | Web UI Temporal — http://localhost:8080 |
| `postgresql` | `postgres:16` | `5433` | БД Temporal + БД сервиса `workflow-db` |


```bash
docker compose -f docker/docker-compose.yml up -d
```

### 2. Сервис

```bash
./gradlew :workflow-service:bootRun
```

## Проверка через `workflow-service/http/workflow-api.http`

End-to-end сценарий проверки — файл `workflow-service/http/workflow-api.http` (формат IntelliJ HTTP Client; в VS Code — расширение REST Client). Он содержит 4 запроса, которые нужно выполнять **строго по порядку**: response-handler каждого запроса сохраняет ID в переменные `workflowId` / `definitionId`, которые используются в следующих запросах.

| # | Запрос | Endpoint | Что делает                                                                                  |
|---|---|---|---------------------------------------------------------------------------------------------|
| 1 | Create workflow | `POST /api/v1/workflows` | Создаёт workflow «HR screening»; `id` из ответа → переменная `workflowId`                   |
| 2 | Save draft definition | `PUT /api/v1/workflows/{{workflowId}}/definitions` | Сохраняет черновик схемы из `http/schemes/hr-scheme.json`; `id` → переменная `definitionId` |
| 3 | Publish definition | `POST /api/v1/workflows/{{workflowId}}/definitions/{{definitionId}}/publish` | Публикует схему                                                                             |
| 4 | Start workflow | `POST /api/v1/workflows/{{workflowId}}/start` | Запускает workflow                                   |

Порядок действий:

1. Открыть файл в IDE и выполнить запросы по очереди.
2. Проверить ответы (все — `200 OK`)
3. Убедиться, что workflow исполнился:
   - в логах сервиса — последовательное выполнение activity (`AI_AGENT` → `CONDITION` → `REST_CALL`);
   - в Temporal UI (http://localhost:8080) — запуск workflow со статусом `Completed`.
