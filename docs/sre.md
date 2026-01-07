# SRE для SaveOps

## SLI

- Доступность gateway: доля успешных HTTP-запросов к `api-gateway`.
- Ошибки gRPC: доля gRPC-вызовов с кодами `UNAVAILABLE`, `DEADLINE_EXCEEDED`, `INTERNAL`.
- Задержка gateway: p95 `http_server_requests_seconds`.
- Задержка gRPC: p95 клиентских вызовов gateway к внутренним сервисам.
- Backlog RabbitMQ: количество сообщений в очередях `saveops.notification.events`, `saveops.audit.events`, `saveops.account.round-up`.
- Успешность начисления процентов: наличие `saveops_interest_accrued_total` за расчетный период.

## SLO

- 99.0% HTTP-запросов gateway за 30 дней завершаются 2xx/4xx, без 5xx.
- p95 gateway latency меньше 500 мс за 24 часа.
- p95 gRPC latency меньше 300 мс за 24 часа.
- RabbitMQ backlog для критичных очередей меньше 1000 сообщений в течение 10 минут.
- Ежедневное начисление процентов завершается до 04:00 локального времени.

## Alerting

- `HighGrpcErrorRate`: gRPC error rate выше 5% за 5 минут.
- `HighRabbitBacklog`: очередь RabbitMQ больше 1000 сообщений 10 минут.
- `InterestAccrualMissing`: нет прироста `saveops_interest_accrued_total` после планового окна.
- `HighNotificationFailures`: `saveops_notification_failures_total` растет быстрее 10 ошибок за 5 минут.

