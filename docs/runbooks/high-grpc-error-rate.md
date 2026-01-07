# Runbook: высокий gRPC error rate

## Симптомы

- Gateway возвращает 502/503.
- В логах есть `UNAVAILABLE`, `DEADLINE_EXCEEDED`, `INTERNAL`.

## Проверки

1. Проверьте health gRPC-сервисов через HTTP actuator: `account-service:8081/actuator/health`, `goal-service:8082/actuator/health`.
2. Проверьте, что порты `9091` и `9092` доступны внутри docker network.
3. Проверьте PostgreSQL и Redis, если ошибки идут из account-service.
4. Сравните p95 latency и retry rate в Grafana.

## Действия

- Перезапустите недоступный сервис.
- Увеличьте deadline только после проверки зависимостей.
- Если проблема в БД, устраните slow queries или connection pool exhaustion.
- Для временной деградации gateway уже возвращает fallback для audit/simulator HTTP-вызовов.

