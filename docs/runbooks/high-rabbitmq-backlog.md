# Runbook: высокий RabbitMQ backlog

## Симптомы

- Очереди `saveops.notification.events`, `saveops.audit.events` или `saveops.account.round-up` растут.
- Уведомления, аудит или round-up операции запаздывают.

## Проверки

1. Откройте RabbitMQ Management: `http://localhost:15672`, логин `saveops/saveops`.
2. Проверьте, какая очередь растет быстрее всего.
3. Проверьте логи потребителя: `make logs service=notification-service` или нужный сервис.
4. Проверьте `/actuator/health` потребителя.

## Действия

- Если потребитель упал, перезапустите сервис: `docker compose up -d notification-service`.
- Если много poison messages, проверьте DLQ и payload события.
- Если backlog связан с нагрузкой, увеличьте количество replicas сервиса-потребителя.
- Если растет `saveops.account.round-up`, проверьте доступность PostgreSQL и Redis у `account-service`.

